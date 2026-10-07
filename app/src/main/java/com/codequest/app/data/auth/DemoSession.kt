package com.codequest.app.data.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Local presentation session.
 *
 * CodeQuest has to be demonstrable without a network, a Firebase project or a
 * Google account. This flag records that the learner chose the clearly
 * labelled offline demo path, so an app restart re-enters the prototype
 * instead of dropping back to the sign-in screen mid-presentation.
 *
 * It is *not* an authentication bypass: it never produces a Firebase user,
 * never grants access to a remote account, and is cleared by sign-out.
 */
object DemoSession {

    private const val PREFS_NAME = "codequest_demo"
    private const val KEY_ACTIVE = "demo_active"

    private var preferences: SharedPreferences? = null

    @Volatile
    private var active = false

    /** True while the local demo learner is signed in. */
    val isActive: Boolean get() = active

    /** Call once from the Activity before the first composition. */
    fun initialize(context: Context) {
        if (preferences != null) return
        val appContext = context.applicationContext
        preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        active = preferences?.getBoolean(KEY_ACTIVE, false) ?: false
    }

    fun setActive(value: Boolean) {
        active = value
        preferences?.edit()?.putBoolean(KEY_ACTIVE, value)?.apply()
    }
}
