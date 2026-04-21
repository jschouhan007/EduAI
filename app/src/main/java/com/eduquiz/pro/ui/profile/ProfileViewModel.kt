// FILE: app/src/main/java/com/eduquiz/pro/ui/profile/ProfileViewModel.kt
package com.eduquiz.pro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduquiz.pro.data.repository.QuizRepository
import com.eduquiz.pro.util.SessionManager
import com.eduquiz.pro.util.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val quizRepository: QuizRepository,
    private val themeManager: ThemeManager
) : ViewModel() {

    fun isGuest(): Boolean = sessionManager.isGuest()

    fun getEmail(): String = if (isGuest()) "" else sessionManager.getUserEmail()

    fun getDisplayName(): String = if (isGuest()) "Guest User" else "Admin"

    fun getInitial(): String = getDisplayName().first().uppercase()

    fun logout() = sessionManager.clearSession()

    fun resetAllProgress() {
        if (isGuest()) return
        viewModelScope.launch { quizRepository.clearProgress() }
    }

    fun getSavedTheme(): ThemeManager.ThemeMode = themeManager.getSavedTheme()
    fun applyTheme(mode: ThemeManager.ThemeMode) = themeManager.applyTheme(mode)
}
