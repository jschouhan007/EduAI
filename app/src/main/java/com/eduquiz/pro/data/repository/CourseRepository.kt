// FILE: app/src/main/java/com/eduquiz/pro/data/repository/CourseRepository.kt
package com.eduquiz.pro.data.repository

import com.eduquiz.pro.data.local.CourseDao
import com.eduquiz.pro.data.model.Course
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val courseDao: CourseDao
) {
    fun getCourses(): Flow<List<Course>> = courseDao.getAllCourses()

    fun searchCourses(query: String): Flow<List<Course>> =
        if (query.isBlank()) courseDao.getAllCourses() else courseDao.searchCourses(query)

    suspend fun getCourse(id: Int): Course? = courseDao.getById(id)
}
