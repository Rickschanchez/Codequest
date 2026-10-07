package com.codequest.app.data

import com.codequest.app.data.auth.AuthRepository
import com.codequest.app.data.auth.FirebaseAuthRepository
import com.codequest.app.domain.model.Achievement
import com.codequest.app.domain.model.CodingChallenge
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.Lesson
import com.codequest.app.domain.model.LearningProfile
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.domain.model.UserProgress
import com.codequest.app.domain.practice.AdaptiveResult
import kotlinx.coroutines.flow.StateFlow

/**
 * Read model plus the small set of learner actions the MVP exposes.
 *
 * Implementations own all state mutation; ViewModels only observe and delegate.
 */
interface CodeQuestRepository {

    val userProgress: StateFlow<UserProgress>

    /** The DSA roadmap in curriculum order. */
    val topics: StateFlow<List<DsaTopic>>

    val lessons: StateFlow<List<Lesson>>

    val questions: StateFlow<List<PracticeQuestion>>

    /** Ids of questions solved correctly at least once. */
    val solvedQuestionIds: StateFlow<Set<String>>

    val achievements: StateFlow<List<Achievement>>

    /** Write-code challenges, unlocked by the same level as the question bank. */
    val codingChallenges: StateFlow<List<CodingChallenge>>

    /** Claims the daily challenge once per session. Returns true when XP was granted. */
    fun claimDailyQuest(): Boolean

    /** Marks a lesson completed and awards its XP once. Returns true on a real transition. */
    fun completeLesson(lessonId: String): Boolean

    /**
     * Submits an answer for a practice question.
     *
     * Returns true when the answer is correct. XP and the solved state are only
     * granted the first time a question is answered correctly.
     */
    fun submitAnswer(questionId: String, selectedIndex: Int): Boolean

    // ------------------------------------------------------ personalised path

    /** Onboarding choices, per-subject levels and the rolling answer window. */
    val learningProfile: StateFlow<LearningProfile>

    /**
     * The latest level verdict the UI should say out loud, e.g.
     * “You're ready for Level 7”. Null when there is nothing to announce.
     */
    val levelChange: StateFlow<AdaptiveResult?>

    /** Persists the onboarding choices and seeds every chosen subject's level. */
    fun completeOnboarding(selectedSubjects: List<String>, initialLevels: Map<String, Int>)

    /**
     * Today's ten-question practice set, pinned to the calendar day so it
     * never reshuffles while the day is running.
     */
    fun dailyQuestions(): List<PracticeQuestion>

    /**
     * Judges the rolling window for [subjectId] and persists any level move.
     *
     * Returns the verdict once enough answers are in the window, null before
     * that — the engine never promotes or demotes on thin evidence.
     */
    fun evaluateProgress(subjectId: String): AdaptiveResult?

    /** Dismisses the announced level verdict. */
    fun clearLevelChange()
}

/**
 * Composition root for the MVP.
 *
 * Deliberately tiny: a single lazily created repository shared by every
 * ViewModel. Swap [MockCodeQuestRepository] for a persistent implementation
 * later without touching the UI layer.
 */
object ServiceLocator {
    val repository: CodeQuestRepository by lazy { MockCodeQuestRepository() }

    /** Auth state is global; every ViewModel delegates to this one instance. */
    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }
}
