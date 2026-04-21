// FILE: app/src/main/java/com/eduquiz/pro/data/local/CourseDao.kt
package com.eduquiz.pro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eduquiz.pro.data.model.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<Course>): List<Long>

    @Query("SELECT * FROM courses ORDER BY title")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE title LIKE '%' || :query || '%' ORDER BY title")
    fun searchCourses(query: String): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Course?

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun count(): Int
}
