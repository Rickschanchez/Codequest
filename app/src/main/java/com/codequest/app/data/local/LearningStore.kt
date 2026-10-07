package com.codequest.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.codequest.app.domain.model.LearningProfile
import com.codequest.app.domain.model.SubjectStats

/**
 * Local persistence for the personalised learning state.
 *
 * Everything that must survive a process death — onboarding choices, the
 * per-subject levels, the rolling answer window and the pinned daily set —
 * lives in one [SharedPreferences] file, encoded with plain text so the store
 * stays inspectable and dependency-free.
 */
object LearningStore {

    private const val PREFS_NAME = "codequest_learning"
    private const val KEY_ONBOARDING = "onboarding_completed"
    private const val KEY_SUBJECTS = "selected_subjects"
    private const val KEY_INITIAL_LEVELS = "initial_levels"
    private const val KEY_CURRENT_LEVELS = "current_levels"
    private const val KEY_STATS = "subject_stats"
    private const val KEY_DAILY_KEY = "daily_key"
    private const val KEY_DAILY_IDS = "daily_ids"

    private const val ENTRY_SEPARATOR = ";"
    private const val FIELD_SEPARATOR = "|"

    @Volatile
    private var preferences: SharedPreferences? = null

    /** Called once from `MainActivity.onCreate`, before any screen composes. */
    fun initialize(context: Context) {
        if (preferences != null) return
        synchronized(this) {
            if (preferences == null) {
                preferences = context.applicationContext
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            }
        }
    }

    val isInitialized: Boolean
        get() = preferences != null

    private fun requirePreferences(): SharedPreferences = checkNotNull(preferences) {
        "LearningStore.initialize(context) must be called before the learning state is read or written."
    }

    /** The saved profile, or null when onboarding has never completed. */
    fun loadProfile(): LearningProfile? {
        val prefs = requirePreferences()
        if (!prefs.contains(KEY_SUBJECTS)) return null

        return LearningProfile(
            onboardingCompleted = prefs.getBoolean(KEY_ONBOARDING, false),
            selectedSubjects = prefs.getString(KEY_SUBJECTS, "")!!
                .split(',').filter { it.isNotBlank() },
            initialLevels = parseLevels(prefs.getString(KEY_INITIAL_LEVELS, "")!!),
            currentLevels = parseLevels(prefs.getString(KEY_CURRENT_LEVELS, "")!!),
            stats = parseStats(prefs.getString(KEY_STATS, "")!!),
        )
    }

    fun saveProfile(profile: LearningProfile) {
        requirePreferences().edit()
            .putBoolean(KEY_ONBOARDING, profile.onboardingCompleted)
            .putString(KEY_SUBJECTS, profile.selectedSubjects.joinToString(","))
            .putString(KEY_INITIAL_LEVELS, formatLevels(profile.initialLevels))
            .putString(KEY_CURRENT_LEVELS, formatLevels(profile.currentLevels))
            .putString(KEY_STATS, formatStats(profile.stats))
            .apply()
    }

    /** The ids pinned to [dayKey], or null when a different day is stored. */
    fun loadDailyIds(dayKey: String): List<String>? {
        val prefs = requirePreferences()
        if (prefs.getString(KEY_DAILY_KEY, null) != dayKey) return null
        return prefs.getString(KEY_DAILY_IDS, "")!!
            .split(',').filter { it.isNotBlank() }
            .takeIf { it.isNotEmpty() }
    }

    /** Pins today's ids so a mid-day level change cannot reshuffle the set. */
    fun saveDailyIds(dayKey: String, ids: List<String>) {
        requirePreferences().edit()
            .putString(KEY_DAILY_KEY, dayKey)
            .putString(KEY_DAILY_IDS, ids.joinToString(","))
            .apply()
    }

    /** Drops the learning state; used when the account context is torn down. */
    fun clear() {
        requirePreferences().edit().clear().apply()
    }

    // ------------------------------------------------------------- encoding

    private fun formatLevels(levels: Map<String, Int>): String =
        levels.entries.joinToString(",") { "${it.key}=${it.value}" }

    private fun parseLevels(raw: String): Map<String, Int> =
        raw.split(',').mapNotNull { entry ->
            val parts = entry.split('=')
            if (parts.size != 2) return@mapNotNull null
            val level = parts[1].toIntOrNull() ?: return@mapNotNull null
            parts[0].takeIf { it.isNotBlank() }?.let { it to level }
        }.toMap()

    private fun formatStats(stats: Map<String, SubjectStats>): String =
        stats.entries.joinToString(ENTRY_SEPARATOR) { (id, s) ->
            listOf(
                id,
                s.attempted.toString(),
                s.correct.toString(),
                s.recent.joinToString("") { if (it) "1" else "0" },
                s.codingAttempts.toString(),
                s.codingCompleted.toString(),
            ).joinToString(FIELD_SEPARATOR)
        }

    private fun parseStats(raw: String): Map<String, SubjectStats> =
        raw.split(ENTRY_SEPARATOR).mapNotNull { entry ->
            val parts = entry.split(FIELD_SEPARATOR)
            if (parts.size < 6 || parts[0].isBlank()) return@mapNotNull null
            val recent = parts[3].map { it == '1' }
            parts[0] to SubjectStats(
                attempted = parts[1].toIntOrNull() ?: 0,
                correct = parts[2].toIntOrNull() ?: 0,
                recent = recent,
                codingAttempts = parts[4].toIntOrNull() ?: 0,
                codingCompleted = parts[5].toIntOrNull() ?: 0,
            )
        }.toMap()
}
