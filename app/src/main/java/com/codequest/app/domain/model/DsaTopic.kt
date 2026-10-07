package com.codequest.app.domain.model

/**
 * One stop on the DSA roadmap.
 *
 * Topics appear in [order] on the Learn screen. Only topics with
 * [isInteractive] are openable in the MVP; the rest render as upcoming
 * roadmap entries.
 *
 * Progress counts are derived by the repository whenever a lesson is
 * completed or a question is solved.
 *
 * @param progress 0f..1f, lessons and questions completed for this topic.
 * @param difficultyDots 1..5 progression indicator for the roadmap.
 */
data class DsaTopic(
    val id: String,
    val name: String,
    val description: String,
    val order: Int,
    val isInteractive: Boolean,
    val difficultyLabel: String,
    val difficultyDots: Int,
    val lessonCount: Int,
    val lessonsCompleted: Int,
    val questionCount: Int,
    val questionsSolved: Int,
    val progress: Float,
) {
    val isUnlocked: Boolean
        get() = isInteractive

    val isCompleted: Boolean
        get() = isInteractive && lessonCount + questionCount > 0 &&
            lessonsCompleted >= lessonCount && questionsSolved >= questionCount

    /** "3 of 7 lessons" style readout. */
    val lessonLabel: String
        get() = "$lessonsCompleted/$lessonCount lessons"

    /** "4 of 10 questions" style readout. */
    val questionLabel: String
        get() = "$questionsSolved/$questionCount questions"
}
