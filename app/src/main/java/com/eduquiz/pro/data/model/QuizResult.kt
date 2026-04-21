// FILE: app/src/main/java/com/eduquiz/pro/data/model/QuizResult.kt
package com.eduquiz.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Int,
    val courseTitle: String,
    val difficulty: Difficulty,
    val score: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val timeoutCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
