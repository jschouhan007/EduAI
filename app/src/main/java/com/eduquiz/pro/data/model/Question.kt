// FILE: app/src/main/java/com/eduquiz/pro/data/model/Question.kt
package com.eduquiz.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Difficulty { EASY, MEDIUM, HARD }

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val difficulty: Difficulty,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val explanation: String
)
