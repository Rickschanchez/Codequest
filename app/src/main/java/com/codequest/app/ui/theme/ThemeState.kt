package com.codequest.app.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Supported appearance modes. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/**
 * Single source of truth for the app appearance.
 *
 * - Starts at [ThemeMode.DARK] (dark-first) until the learner picks a mode.
 * - The Home toggle switches only LIGHT <-> DARK, which overrides the system
 *   appearance (see [setMode]).
 * - The selection is persisted in [SharedPreferences] so an app restart
 *   preserves it.
 */
object ThemeState {
    private const val PREFS_NAME = "codequest_theme"
    private const val KEY_MODE = "theme_mode"

    private var preferences: SharedPreferences? = null

    private var selectedMode by mutableStateOf(ThemeMode.DARK)

    /** Current user-selected mode. */
    val mode: ThemeMode get() = selectedMode

    /** Call once from the Activity before setContent. */
    fun initialize(context: Context) {
        if (preferences != null) return
        val appContext = context.applicationContext
        preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = preferences?.getString(KEY_MODE, ThemeMode.DARK.name)
        selectedMode = runCatching { ThemeMode.valueOf(stored.orEmpty()) }
            .getOrDefault(ThemeMode.SYSTEM)
    }

    /** Persist and apply a new mode. */
    fun setMode(newMode: ThemeMode) {
        if (selectedMode == newMode) return
        selectedMode = newMode
        preferences?.edit()?.putString(KEY_MODE, newMode.name)?.apply()
    }

    /** The Home toggle: flips only between an explicit Light and Dark choice. */
    fun toggle(isDarkCurrently: Boolean) {
        setMode(if (isDarkCurrently) ThemeMode.LIGHT else ThemeMode.DARK)
    }

    /** Whether the app should currently render dark, resolving SYSTEM. */
    @Composable
    fun resolvedIsDark(): Boolean = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
}
