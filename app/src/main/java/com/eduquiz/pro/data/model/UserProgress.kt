// FILE: app/src/main/java/com/eduquiz/pro/data/model/UserProgress.kt
package com.eduquiz.pro.data.model

data class UserProgress(
    val totalQuizzes: Int,
    val averageScore: Int,
    val bestScore: Int,
    val coursesAttempted: Int,
    val recentScores: List<Int>
)
