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
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun getSavedLocale(): String = prefs.getString(KEY_LANGUAGE, "en") ?: "en"

    fun applySavedLocale(context: Context) {
        val code = getSavedLocale()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
    }

    fun wrapContext(base: Context): Context {
        val code = getSavedLocale()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return base
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }

    companion object {
        private const val PREFS_NAME = "eduquiz_language"
        private const val KEY_LANGUAGE = "language_code"
    }
}
