package com.codequest.app.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codequest.app.data.ServiceLocator
import com.codequest.app.data.auth.AuthCancelledException
import com.codequest.app.data.auth.AuthRepository
import com.codequest.app.data.auth.AuthUser
import com.codequest.app.data.auth.authMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Sign-in or account creation. */
enum class AuthMode { SIGN_IN, CREATE }

/** Everything the auth screen renders. */
data class AuthUiState(
    val mode: AuthMode = AuthMode.SIGN_IN,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val isCreate: Boolean get() = mode == AuthMode.CREATE

    private fun validate(): String? = when {
        isCreate && name.isBlank() -> "Tell us your name."
        email.isBlank() || !email.contains('@') -> "Enter a valid email address."
        password.length < 6 -> "Password must be at least 6 characters."
        else -> null
    }

    internal fun validationError(): String? = validate()
}

/**
 * Drives [AuthScreen]. The signed-in user itself lives in the repository so the
 * gate in CodeQuestApp flips the moment Firebase restores a session.
 */
class AuthViewModel(
    private val repository: AuthRepository = ServiceLocator.authRepository,
) : ViewModel() {

    val currentUser: StateFlow<AuthUser?> = repository.currentUser

    val isConfigured: Boolean
        get() = repository.isConfigured

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    fun onNameChange(value: String) = update { it.copy(name = value) }

    fun onEmailChange(value: String) = update { it.copy(email = value) }

    fun onPasswordChange(value: String) = update { it.copy(password = value) }

    fun toggleMode() = update {
        it.copy(
            mode = if (it.isCreate) AuthMode.SIGN_IN else AuthMode.CREATE,
            error = null,
        )
    }

    fun submit() {
        val current = _ui.value
        if (current.isLoading) return

        val validation = current.validationError()
        if (validation != null) {
            update { it.copy(error = validation) }
            return
        }

        update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = if (current.isCreate) {
                repository.signUpWithEmail(current.name, current.email, current.password)
            } else {
                repository.signInWithEmail(current.email, current.password)
            }
            update { state ->
                state.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()
                        ?.takeUnless { it is AuthCancelledException }
                        ?.authMessage(),
                )
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        if (_ui.value.isLoading) return
        update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = repository.signInWithGoogle(context)
            update { state ->
                state.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()
                        ?.takeUnless { it is AuthCancelledException }
                        ?.authMessage(),
                )
            }
        }
    }

    fun signOut() = repository.signOut()

    /** Offline presentation path — local only, never touches Firebase. */
    fun continueAsDemo() = repository.continueAsDemo()

    private fun update(transform: (AuthUiState) -> AuthUiState) {
        _ui.value = transform(_ui.value)
    }
}
