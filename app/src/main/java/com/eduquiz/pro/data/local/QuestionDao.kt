// FILE: app/src/main/java/com/eduquiz/pro/data/local/QuestionDao.kt
package com.eduquiz.pro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eduquiz.pro.data.model.Difficulty
import com.eduquiz.pro.data.model.Question

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions WHERE courseId = :courseId AND difficulty = :difficulty ORDER BY RANDOM() LIMIT 10")
    suspend fun getRandomQuestions(courseId: Int, difficulty: Difficulty): List<Question>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int
}
