// FILE: app/src/main/java/com/eduquiz/pro/di/AppModule.kt
package com.eduquiz.pro.di

import android.content.Context
import com.eduquiz.pro.data.local.AppDatabase
import com.eduquiz.pro.data.local.CourseDao
import com.eduquiz.pro.data.local.QuestionDao
import com.eduquiz.pro.data.local.QuizResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    fun provideCourseDao(database: AppDatabase): CourseDao = database.courseDao()

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao = database.questionDao()

    @Provides
    fun provideQuizResultDao(database: AppDatabase): QuizResultDao = database.quizResultDao()
}
