package com.codequest.app.domain.model

/**
 * A micro lesson: the shortest learning unit inside a [DsaTopic].
 *
 * Each lesson is a single self-contained card with an explanation, a worked
 * example and one key takeaway.
 */
data class Lesson(
    val id: String,
    val topicId: String,
    val order: Int,
    val title: String,
    val explanation: String,
    val example: String,
    val keyTakeaway: String,
    val durationMinutes: Int,
    val xpReward: Int,
    val isCompleted: Boolean = false,
)
