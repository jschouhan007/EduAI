// FILE: app/src/main/java/com/eduquiz/pro/data/repository/AuthRepository.kt
package com.eduquiz.pro.data.repository

import com.eduquiz.pro.util.SessionManager
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String): Boolean {
        delay(1000) // Simulate network request latency.
        val isValid = email == "admin" && password == "admin"
        if (isValid) sessionManager.saveLogin(email)
        return isValid
    }

    fun continueAsGuest() {
        sessionManager.saveGuestLogin()
    }
}
