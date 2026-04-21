// FILE: app/src/main/java/com/eduquiz/pro/ui/home/HomeViewModel.kt
package com.eduquiz.pro.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eduquiz.pro.data.model.Course
import com.eduquiz.pro.data.repository.CourseRepository
import com.eduquiz.pro.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _courses = MutableLiveData<List<Course>>(emptyList())
    val courses: LiveData<List<Course>> = _courses

    private var searchJob: Job? = null

    val isGuest: Boolean get() = sessionManager.isGuest()

    val greetingName: String
        get() = if (sessionManager.isGuest()) "Guest" else "Admin"

    init {
        search("")
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            courseRepository.searchCourses(query).collectLatest { _courses.postValue(it) }
        }
    }
}
