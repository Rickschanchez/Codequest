package com.codequest.app.domain.model

/**
 * Everything the app knows about the local learner.
 *
 * XP is tracked twice: [totalXp] is lifetime, while [xpIntoLevel] is the amount
 * earned inside the current level against [xpLevelCap]. The daily challenge,
 * streak and solved counters form the subtle RPG layer on top of the learning
 * loop.
 */
data class UserProgress(
    val displayName: String,
    val greeting: String,
    val level: Int,
    val totalXp: Int,
    val xpIntoLevel: Int,
    val xpLevelCap: Int,
    val streakDays: Int,
    val dailyChallengeCompleted: Boolean,
    val dailyChallengeXp: Int,
    val lessonsCompleted: Int,
    val questionsSolved: Int,
    val easySolved: Int,
    val mediumSolved: Int,
    val hardSolved: Int,
    val dsaProgress: Float,
    val continueLesson: Lesson?,
    val recentlySolvedTitle: String?,
) {
    val levelProgress: Float
        get() = if (xpLevelCap <= 0) 0f else (xpIntoLevel.toFloat() / xpLevelCap).coerceIn(0f, 1f)

    val xpToNextLevel: Int
        get() = (xpLevelCap - xpIntoLevel).coerceAtLeast(0)
}
