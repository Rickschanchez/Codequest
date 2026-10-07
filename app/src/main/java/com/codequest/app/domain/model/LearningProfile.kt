package com.codequest.app.domain.model

/**
 * Per-subject performance, kept locally so the adaptive engine can read a
 * rolling window of recent answers without touching the network.
 *
 * @param recent newest-last booleans for the last answers given in this subject.
 */
data class SubjectStats(
    val attempted: Int = 0,
    val correct: Int = 0,
    val recent: List<Boolean> = emptyList(),
    val codingAttempts: Int = 0,
    val codingCompleted: Int = 0,
) {
    val accuracy: Float
        get() = if (attempted == 0) 0f else correct.toFloat() / attempted

    /** Accuracy over the last [window] answers; 0f when nothing was answered. */
    fun recentAccuracy(window: Int): Float {
        val slice = recent.takeLast(window)
        return if (slice.isEmpty()) 0f else slice.count { it }.toFloat() / slice.size
    }

    fun answeredInWindow(window: Int): Int = recent.takeLast(window).size
}

/**
 * The personalised learning profile produced by onboarding and refined by
 * practice.
 *
 * Levels are strictly per subject — DSA can sit at 6 while SQL sits at 3 —
 * and [initialLevels] preserves the self-assessment so later adaptation is
 * always measured against the learner's own starting point.
 */
data class LearningProfile(
    val onboardingCompleted: Boolean = false,
    val selectedSubjects: List<String> = emptyList(),
    val initialLevels: Map<String, Int> = emptyMap(),
    val currentLevels: Map<String, Int> = emptyMap(),
    val stats: Map<String, SubjectStats> = emptyMap(),
) {

    fun levelFor(subjectId: String): Int =
        currentLevels[subjectId] ?: initialLevels[subjectId] ?: DEFAULT_LEVEL

    fun initialLevelFor(subjectId: String): Int =
        initialLevels[subjectId] ?: DEFAULT_LEVEL

    fun statsFor(subjectId: String): SubjectStats = stats[subjectId] ?: SubjectStats()

    fun hasSubject(subjectId: String): Boolean = subjectId in selectedSubjects

    /** Mean level across the chosen subjects, used for the Profile summary. */
    val averageLevel: Float
        get() = if (selectedSubjects.isEmpty()) {
            0f
        } else {
            selectedSubjects.map { levelFor(it) }.average().toFloat()
        }

    /** 0f..1f progress of the whole path, where level 10 is mastery. */
    val pathProgress: Float
        get() = if (selectedSubjects.isEmpty()) {
            0f
        } else {
            (averageLevel - 1f) / 9f
        }

    companion object {
        const val DEFAULT_LEVEL = 1
        const val MIN_LEVEL = 1
        const val MAX_LEVEL = 10
    }
}

/** Self-assessment options offered during onboarding, in ascending order. */
enum class KnowledgeBand(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val level: Int,
) {
    BEGINNER("\uD83C\uDF31", "Beginner", "I'm starting from scratch.", 1),
    AMATEUR("\uD83D\uDD25", "Amateur", "I know the basics and have practiced a little.", 3),
    SEMI_PRO("⚡", "Semi-Pro", "I can solve problems, but I still get stuck.", 6),
    MASTER("\uD83D\uDC51", "Master", "Bring on the hard stuff.", 8);

    companion object {
        fun forLevel(level: Int): KnowledgeBand =
            entries.lastOrNull { level >= it.level } ?: BEGINNER
    }
}
