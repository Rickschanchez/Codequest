package com.codequest.app.domain.practice

import com.codequest.app.domain.model.LearningProfile
import com.codequest.app.domain.model.SubjectStats

/** Which way the engine wants the level to move. */
enum class LevelDirection { UP, DOWN, HOLD }

/**
 * The outcome of judging one subject's rolling window: where the level was,
 * where it lands and the sentence the UI should say about it.
 */
data class AdaptiveResult(
    val subjectId: String,
    val fromLevel: Int,
    val toLevel: Int,
    val direction: LevelDirection,
    val answered: Int,
    val accuracy: Float,
    val message: String,
) {
    val levelChanged: Boolean get() = toLevel != fromLevel
}

/**
 * The rule that moves a learner between levels.
 *
 * Judged over the last [WINDOW] answers only, so one bad afternoon cannot
 * undo weeks of good work — but a genuine streak of misses can. Nothing here
 * touches persistence or the profile: it takes the stats, returns a verdict.
 */
object AdaptiveEngine {

    /** Answers considered by one judgement; older ones fall out of the window. */
    const val WINDOW = 20

    /** No judgement without this many answers in the window (10 counted). */
    const val MIN_ANSWERS = 10

    /** Accuracy at or above which the learner is promoted. */
    const val PROMOTE_AT = 0.8f

    /** Accuracy strictly below which the learner is demoted. */
    const val DEMOTE_BELOW = 0.5f

    /** Evaluates [stats] against [currentLevel] and returns the verdict. */
    fun evaluate(
        stats: SubjectStats,
        currentLevel: Int,
        subjectId: String,
        minLevel: Int = LearningProfile.MIN_LEVEL,
        maxLevel: Int = LearningProfile.MAX_LEVEL,
    ): AdaptiveResult {
        val answered = stats.answeredInWindow(WINDOW)
        val accuracy = stats.recentAccuracy(WINDOW)

        val direction = when {
            answered < MIN_ANSWERS -> LevelDirection.HOLD
            accuracy >= PROMOTE_AT -> LevelDirection.UP
            accuracy < DEMOTE_BELOW -> LevelDirection.DOWN
            else -> LevelDirection.HOLD
        }

        val toLevel = (currentLevel + when (direction) {
            LevelDirection.UP -> 1
            LevelDirection.DOWN -> -1
            LevelDirection.HOLD -> 0
        }).coerceIn(minLevel, maxLevel)

        return AdaptiveResult(
            subjectId = subjectId,
            fromLevel = currentLevel,
            toLevel = toLevel,
            direction = direction,
            answered = answered,
            accuracy = accuracy,
            message = when {
                answered < MIN_ANSWERS -> {
                    val missing = MIN_ANSWERS - answered
                    "$missing more answer${if (missing == 1) "" else "s"} " +
                        "before your level is tuned."
                }
                direction == LevelDirection.UP ->
                    "You're ready for Level $toLevel \uD83D\uDD25"
                direction == LevelDirection.DOWN ->
                    "Let's strengthen the fundamentals \uD83D\uDCAA"
                else ->
                    "Level $toLevel is the right fit for now."
            },
        )
    }
}
