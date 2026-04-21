// FILE: app/src/main/java/com/eduquiz/pro/util/SessionManager.kt
package com.eduquiz.pro.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLogin(email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, email)
            .putBoolean(KEY_IS_GUEST, false)
            .apply()
    }

    fun saveGuestLogin() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, "")
            .putBoolean(KEY_IS_GUEST, true)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun isGuest(): Boolean = prefs.getBoolean(KEY_IS_GUEST, false)

    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "eduquiz_session"
        const val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"
        const val KEY_USER_EMAIL = "USER_EMAIL"
        const val KEY_IS_GUEST = "IS_GUEST"
    }
}
