// FILE: app/src/main/java/com/eduquiz/pro/data/local/QuizResultDao.kt
package com.eduquiz.pro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eduquiz.pro.data.model.QuizResult
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: QuizResult)

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<QuizResult>>

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentResults(limit: Int): List<QuizResult>

    @Query("DELETE FROM quiz_results")
    suspend fun clearAll()
}
