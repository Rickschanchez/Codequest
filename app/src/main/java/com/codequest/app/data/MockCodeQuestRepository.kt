package com.codequest.app.data

import com.codequest.app.data.auth.AuthUser
import com.codequest.app.data.local.LearningStore
import com.codequest.app.domain.model.Achievement
import com.codequest.app.domain.model.CodingChallenge
import com.codequest.app.domain.model.Difficulty
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.LearningProfile
import com.codequest.app.domain.model.Lesson
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.domain.model.SubjectStats
import com.codequest.app.domain.model.UserProgress
import com.codequest.app.domain.practice.AdaptiveEngine
import com.codequest.app.domain.practice.AdaptiveResult
import com.codequest.app.domain.practice.DailyPractice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * In-memory repository seeded from [MockData].
 *
 * Every mutation flows through here so XP, level ups, lesson completion,
 * solved state, topic progress and achievement unlocks stay consistent across
 * all four screens.
 */
class MockCodeQuestRepository : CodeQuestRepository {

    private val _userProgress = MutableStateFlow(MockData.userProgress())
    private val _topics = MutableStateFlow(MockData.topics(MockData.lessons, MockData.solvedQuestionIds))
    private val _lessons = MutableStateFlow(MockData.lessons)
    private val _questions = MutableStateFlow(MockData.questions)
    private val _solvedQuestionIds = MutableStateFlow(MockData.solvedQuestionIds)
    private val _achievements = MutableStateFlow(buildAchievements())
    private val _codingChallenges = MutableStateFlow(CodingChallenges.all)

    /**
     * The personalised path: restored from disk when it exists, otherwise the
     * untouched profile that sends the learner into onboarding.
     */
    private val _learningProfile = MutableStateFlow(
        LearningStore.loadProfile() ?: LearningProfile(),
    )

    /** The level verdict waiting to be announced; cleared once the UI shows it. */
    private val _levelChange = MutableStateFlow<AdaptiveResult?>(null)

    /** First attempt per question feeds the window; repeats cannot game it. */
    private val recordedQuestionIds = mutableSetOf<String>()

    override val learningProfile: StateFlow<LearningProfile> = _learningProfile.asStateFlow()
    override val levelChange: StateFlow<AdaptiveResult?> = _levelChange.asStateFlow()

    override val userProgress: StateFlow<UserProgress> = _userProgress.asStateFlow()
    override val topics: StateFlow<List<DsaTopic>> = _topics.asStateFlow()
    override val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()
    override val questions: StateFlow<List<PracticeQuestion>> = _questions.asStateFlow()
    override val solvedQuestionIds: StateFlow<Set<String>> = _solvedQuestionIds.asStateFlow()
    override val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    override val codingChallenges: StateFlow<List<CodingChallenge>> =
        _codingChallenges.asStateFlow()

    /**
     * Keeps the dashboard identity honest: whichever account is signed in —
     * Firebase or the offline demo — is the name Home greets and Profile shows.
     * The seeded value stays in place while nobody is signed in.
     */
    private val identityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        ServiceLocator.authRepository.currentUser
            .onEach(::applyIdentity)
            .launchIn(identityScope)
    }

    private fun applyIdentity(user: AuthUser?) {
        val name = user?.greetingName?.takeIf { it.isNotBlank() } ?: return
        val current = _userProgress.value
        if (current.displayName == name) return
        _userProgress.value = current.copy(
            displayName = name,
            greeting = MockData.greetingFor(name),
        )
    }

    override fun claimDailyQuest(): Boolean {
        val current = _userProgress.value
        if (current.dailyChallengeCompleted) return false

        _userProgress.value = grantXp(current, current.dailyChallengeXp)
            .copy(dailyChallengeCompleted = true)
        refreshAchievements()
        return true
    }

    override fun completeLesson(lessonId: String): Boolean {
        val lesson = _lessons.value.firstOrNull { it.id == lessonId } ?: return false
        if (lesson.isCompleted) return false

        _lessons.value = _lessons.value.map {
            if (it.id == lessonId) it.copy(isCompleted = true) else it
        }
        _userProgress.value = grantXp(_userProgress.value, lesson.xpReward)

        refreshTopics()
        syncDerivedState()
        refreshAchievements()
        return true
    }

    override fun submitAnswer(questionId: String, selectedIndex: Int): Boolean {
        val question = _questions.value.firstOrNull { it.id == questionId } ?: return false
        val isCorrect = selectedIndex == question.correctIndex
        recordAnswer(questionId, isCorrect)
        if (!isCorrect || questionId in _solvedQuestionIds.value) return isCorrect

        _solvedQuestionIds.value = _solvedQuestionIds.value + questionId
        // One pass over the bank: counts without building an intermediate list.
        val solvedIds = _solvedQuestionIds.value
        var solvedTotal = 0
        var easy = 0
        var medium = 0
        var hard = 0
        _questions.value.forEach { candidate ->
            if (candidate.id in solvedIds) {
                solvedTotal++
                when (candidate.difficulty) {
                    Difficulty.EASY -> easy++
                    Difficulty.MEDIUM -> medium++
                    Difficulty.HARD -> hard++
                }
            }
        }

        _userProgress.value = grantXp(
            _userProgress.value.copy(
                questionsSolved = solvedTotal,
                easySolved = easy,
                mediumSolved = medium,
                hardSolved = hard,
                recentlySolvedTitle = question.question,
            ),
            question.xpReward,
        )

        refreshTopics()
        syncDerivedState()
        refreshAchievements()
        return true
    }

    override fun completeOnboarding(
        selectedSubjects: List<String>,
        initialLevels: Map<String, Int>,
    ) {
        if (selectedSubjects.isEmpty()) return

        updateProfile {
            LearningProfile(
                onboardingCompleted = true,
                selectedSubjects = selectedSubjects,
                initialLevels = initialLevels,
                currentLevels = initialLevels,
                stats = selectedSubjects.associateWith { SubjectStats() },
            )
        }
    }

    /** The bank never changes, so the id index is built once per process. */
    private val questionsById: Map<String, PracticeQuestion> by lazy {
        MockData.questions.associateBy { it.id }
    }

    override fun dailyQuestions(): List<PracticeQuestion> {
        val dayKey = DailyPractice.todayKey()
        val questions = _questions.value

        // A set pinned earlier today survives level changes until tomorrow.
        LearningStore.loadDailyIds(dayKey)?.let { pinned ->
            val byId = questionsById
            val restored = pinned.mapNotNull { byId[it] }
            if (restored.isNotEmpty()) return restored
        }

        val picked = DailyPractice.pick(
            pool = questions,
            level = _learningProfile.value.levelFor(SUBJECT_DSA),
            dayKey = dayKey,
        )
        LearningStore.saveDailyIds(dayKey, picked.map { it.id })
        return picked
    }

    override fun evaluateProgress(subjectId: String): AdaptiveResult? {
        val profile = _learningProfile.value
        val stats = profile.statsFor(subjectId)
        val verdict = AdaptiveEngine.evaluate(
            stats = stats,
            currentLevel = profile.levelFor(subjectId),
            subjectId = subjectId,
        )

        // Thin evidence is no evidence: keep accumulating until the window fills.
        if (verdict.answered < AdaptiveEngine.MIN_ANSWERS) return null

        updateProfile {
            it.copy(
                currentLevels = if (verdict.levelChanged) {
                    it.currentLevels + (subjectId to verdict.toLevel)
                } else {
                    it.currentLevels
                },
                // The judgement closes the window; the next one needs fresh answers.
                stats = it.stats + (subjectId to stats.copy(recent = emptyList())),
            )
        }
        if (verdict.levelChanged) _levelChange.value = verdict
        return verdict
    }

    override fun clearLevelChange() {
        _levelChange.value = null
    }

    /** Feeds one first-time attempt into the subject's rolling window. */
    private fun recordAnswer(questionId: String, correct: Boolean) {
        if (!recordedQuestionIds.add(questionId)) return

        val profile = _learningProfile.value
        val stats = profile.statsFor(SUBJECT_DSA)
        updateProfile {
            it.copy(
                stats = it.stats + (SUBJECT_DSA to stats.copy(
                    attempted = stats.attempted + 1,
                    correct = stats.correct + if (correct) 1 else 0,
                    recent = (stats.recent + correct).takeLast(RECENT_WINDOW_LIMIT),
                )),
            )
        }
    }

    /** Applies [transform] and writes the result straight to disk. */
    private fun updateProfile(transform: (LearningProfile) -> LearningProfile) {
        val updated = transform(_learningProfile.value)
        _learningProfile.value = updated
        LearningStore.saveProfile(updated)
    }

    /** Rebuilds roadmap progress from the live lesson and solved state. */
    private fun refreshTopics() {
        _topics.value = MockData.topics(_lessons.value, _solvedQuestionIds.value)
    }

    /** Keeps the fields that are derived from lessons, topics and questions truthful. */
    private fun syncDerivedState() {
        val interactive = _topics.value.filter { it.isInteractive }
        val dsaProgress = if (interactive.isEmpty()) {
            0f
        } else {
            interactive.map { it.progress }.average().toFloat().coerceIn(0f, 1f)
        }

        _userProgress.value = _userProgress.value.copy(
            lessonsCompleted = _lessons.value.count { it.isCompleted },
            dsaProgress = dsaProgress,
            continueLesson = MockData.orderedLessons(_lessons.value).firstOrNull { !it.isCompleted },
        )
    }

    /** Adds XP and rolls over into the next level as many times as needed. */
    private fun grantXp(progress: UserProgress, amount: Int): UserProgress {
        if (amount <= 0) return progress
        var into = progress.xpIntoLevel + amount
        var level = progress.level
        val cap = progress.xpLevelCap

        while (into >= cap) {
            into -= cap
            level += 1
        }

        return progress.copy(
            level = level,
            xpIntoLevel = into,
            totalXp = progress.totalXp + amount,
        )
    }

    private fun refreshAchievements() {
        _achievements.value = buildAchievements()
    }

    private fun buildAchievements(): List<Achievement> {
        val progress = _userProgress.value
        val arraysLessons = _lessons.value.filter { it.topicId == TOPIC_ARRAYS }

        return listOf(
            achievement(
                id = "first-question",
                title = "First Steps",
                description = "Solve your first practice question.",
                value = progress.questionsSolved.coerceAtMost(1),
                target = 1,
                xpReward = 50,
            ),
            achievement(
                id = "on-fire",
                title = "On Fire",
                description = "Maintain a 7 day streak.",
                value = progress.streakDays.coerceAtMost(STREAK_TARGET),
                target = STREAK_TARGET,
                xpReward = 100,
            ),
            achievement(
                id = "easy-does-it",
                title = "Easy Does It",
                description = "Solve 5 Easy questions.",
                value = progress.easySolved.coerceAtMost(EASY_TARGET),
                target = EASY_TARGET,
                xpReward = 100,
            ),
            achievement(
                id = "array-apprentice",
                title = "Array Apprentice",
                description = "Complete every Arrays lesson.",
                value = arraysLessons.count { it.isCompleted },
                target = arraysLessons.size,
                xpReward = 150,
            ),
            achievement(
                id = "problem-solver",
                title = "Problem Solver",
                description = "Solve 5 Medium questions.",
                value = progress.mediumSolved.coerceAtMost(MEDIUM_TARGET),
                target = MEDIUM_TARGET,
                xpReward = 150,
            ),
            achievement(
                id = "sorting-specialist",
                title = "Sorting Specialist",
                description = "Solve 3 Sorting questions.",
                value = solvedInTopic(TOPIC_SORTING),
                target = SORTING_TARGET,
                xpReward = 150,
            ),
            achievement(
                id = "graph-explorer",
                title = "Graph Explorer",
                description = "Complete your first Graphs lesson.",
                value = _lessons.value.count { it.topicId == TOPIC_GRAPHS && it.isCompleted },
                target = 1,
                xpReward = 200,
            ),
        )
    }

    /** Questions solved so far inside one roadmap topic. */
    private fun solvedInTopic(topicId: String): Int =
        MockData.questionsInTopic(topicId).count { it.id in _solvedQuestionIds.value }

    private fun achievement(
        id: String,
        title: String,
        description: String,
        value: Int,
        target: Int,
        xpReward: Int,
    ): Achievement {
        val safeTarget = target.coerceAtLeast(1)
        val clamped = value.coerceAtMost(safeTarget)
        return Achievement(
            id = id,
            title = title,
            description = description,
            progress = clamped.toFloat() / safeTarget,
            progressLabel = "$clamped/$safeTarget",
            isUnlocked = clamped >= safeTarget,
            xpReward = xpReward,
        )
    }

    private companion object {
        const val TOPIC_ARRAYS = "arrays"
        const val TOPIC_SORTING = "sorting"
        const val TOPIC_GRAPHS = "graphs"
        const val SUBJECT_DSA = "dsa"
        const val RECENT_WINDOW_LIMIT = 50
        const val STREAK_TARGET = 7
        const val EASY_TARGET = 5
        const val MEDIUM_TARGET = 5
        const val SORTING_TARGET = 3
    }
}
