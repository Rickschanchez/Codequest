package com.codequest.app.domain.model

/**
 * A free-form coding task: write a solution in the editor and run it against
 * [spec] test cases.
 *
 * Challenges unlock at their [level], so the coding ramp (6 read → 7 build →
 * 8 debug → 9 implement → 10 master) follows the same progression as the
 * question bank.
 */
data class CodingChallenge(
    val id: String,
    val topicId: String,
    val level: Int,
    val title: String,
    val summary: String,
    val spec: CodingSpec,
    val xpReward: Int,
)
