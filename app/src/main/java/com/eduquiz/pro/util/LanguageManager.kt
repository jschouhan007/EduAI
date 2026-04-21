// FILE: app/src/main/java/com/eduquiz/pro/util/LanguageManager.kt
package com.eduquiz.pro.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setLocale(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    fun getSavedLocale(): String = prefs.getString(KEY_LANGUAGE, "en") ?: "en"

    fun applySavedLocale(context: Context) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(getSavedLocale()))
    }

    fun wrapContext(base: Context): Context = wrapContextWithCode(base, getSavedLocale())

    companion object {
        private const val PREFS_NAME = "eduquiz_language"
        private const val KEY_LANGUAGE = "language_code"

        fun wrapContext(base: Context): Context {
            val prefs = base.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val code = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
            return wrapContextWithCode(base, code)
        }

        private fun wrapContextWithCode(base: Context, code: String): Context {
            // On Android 13+, AppCompatDelegate.setApplicationLocales handles per-app locales natively.
            // Returning base context here avoids conflicting manual context wrapping on newer versions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return base
            val locale = Locale(code)
            Locale.setDefault(locale)
            val config = Configuration(base.resources.configuration)
            config.setLocale(locale)
            return base.createConfigurationContext(config)
        }
    }
}
