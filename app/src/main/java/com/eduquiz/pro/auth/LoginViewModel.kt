// FILE: app/src/main/java/com/eduquiz/pro/auth/LoginViewModel.kt
package com.eduquiz.pro.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduquiz.pro.R
import com.eduquiz.pro.data.repository.AuthRepository
import com.eduquiz.pro.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    data class LoginUiState(
        val isLoading: Boolean = false,
        val emailErrorRes: Int? = null,
        val passwordErrorRes: Int? = null,
        val authErrorRes: Int? = null,
        val loginSuccess: Boolean = false
    )

    private val _state = MutableLiveData(LoginUiState())
    val state: LiveData<LoginUiState> = _state

    fun clearErrors() {
        _state.value = _state.value?.copy(emailErrorRes = null, passwordErrorRes = null, authErrorRes = null)
    }

    fun login(email: String, password: String) {
        var emailError: Int? = null
        var passwordError: Int? = null

        if (email.isBlank()) emailError = R.string.error_email_required
        if (password.isBlank()) passwordError = R.string.error_password_required

        if (emailError != null || passwordError != null) {
            _state.value = LoginUiState(emailErrorRes = emailError, passwordErrorRes = passwordError)
            return
        }

        _state.value = LoginUiState(isLoading = true)
        viewModelScope.launch {
            val ok = authRepository.login(email.trim(), password)
            _state.value = if (ok) {
                LoginUiState(loginSuccess = true)
            } else {
                LoginUiState(authErrorRes = R.string.error_invalid_credentials)
            }
        }
    }

    fun continueAsGuest() {
        authRepository.continueAsGuest()
        _state.value = LoginUiState(loginSuccess = true)
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}
