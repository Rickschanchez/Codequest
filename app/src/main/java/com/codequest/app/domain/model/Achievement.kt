package com.codequest.app.domain.model

/**
 * A milestone reward.
 *
 * @param progress 0f..1f completion ratio.
 * @param progressLabel display form, e.g. `"6/7"`.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val progress: Float,
    val progressLabel: String,
    val isUnlocked: Boolean,
    val xpReward: Int,
)