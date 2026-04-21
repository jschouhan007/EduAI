// FILE: app/src/main/java/com/eduquiz/pro/data/repository/QuizRepository.kt
package com.eduquiz.pro.data.repository

import com.eduquiz.pro.data.local.QuestionDao
import com.eduquiz.pro.data.local.QuizResultDao
import com.eduquiz.pro.data.model.Difficulty
import com.eduquiz.pro.data.model.Question
import com.eduquiz.pro.data.model.QuizResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val questionDao: QuestionDao,
    private val resultDao: QuizResultDao
) {
    suspend fun getQuizQuestions(courseId: Int, difficulty: Difficulty): List<Question> =
        questionDao.getRandomQuestions(courseId, difficulty)

    suspend fun saveResult(result: QuizResult) = resultDao.insert(result)

    fun getResults(): Flow<List<QuizResult>> = resultDao.getAllResults()

    suspend fun getRecentScores(limit: Int): List<QuizResult> = resultDao.getRecentResults(limit)

    suspend fun clearProgress() = resultDao.clearAll()
}
