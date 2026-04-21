// FILE: app/src/main/java/com/eduquiz/pro/EduQuizApp.kt
package com.eduquiz.pro

import android.app.Application
import com.eduquiz.pro.util.LanguageManager
import com.eduquiz.pro.util.ThemeManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EduQuizApp : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var languageManager: LanguageManager

    override fun onCreate() {
        super.onCreate()
        // Apply persisted appearance and locale as early as possible for app-wide consistency.
        themeManager.applyTheme(themeManager.getSavedTheme())
        languageManager.applySavedLocale(this)
    }
}
