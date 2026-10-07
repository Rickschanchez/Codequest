package com.codequest.app.data.auth

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

/** The signed-in learner. Deliberately small — enough to greet and identify. */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    /**
     * True for the offline presentation account. That path never talks to
     * Firebase, so the UI labels it honestly instead of implying a real
     * sign-in happened.
     */
    val isDemo: Boolean = false,
) {
    /** Name to greet with: display name first, otherwise the email local part. */
    val greetingName: String
        get() = displayName?.takeIf { it.isNotBlank() }
            ?: email?.substringBefore('@')?.takeIf { it.isNotBlank() }
            ?: "Learner"
}

/** Raised when the learner dismisses the Google account picker — not an error. */
class AuthCancelledException : Exception("Cancelled")

/**
 * Authentication boundary for the app.
 *
 * Firestore sync lands later: the stable [AuthUser.uid] is what the CodeQuest
 * profile document will be keyed by.
 */
interface AuthRepository {

    /** Emits on sign-in and sign-out; `null` means signed out. */
    val currentUser: StateFlow<AuthUser?>

    /**
     * False until google-services.json has been dropped into the app module.
     * The auth screen surfaces this instead of crashing on a missing config.
     */
    val isConfigured: Boolean

    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>

    suspend fun signUpWithEmail(name: String, email: String, password: String): Result<AuthUser>

    /** Opens the Google account picker via Credential Manager, then signs in. */
    suspend fun signInWithGoogle(context: Context): Result<AuthUser>

    /**
     * Enters the clearly labelled offline presentation account.
     *
     * Local only — no Firebase user is created and no credential is forged.
     * [signOut] leaves the session.
     */
    fun continueAsDemo()

    fun signOut()
}
