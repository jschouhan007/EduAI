// FILE: app/src/main/java/com/eduquiz/pro/ui/progress/ProgressViewModel.kt
package com.eduquiz.pro.ui.progress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduquiz.pro.data.model.QuizResult
import com.eduquiz.pro.data.model.UserProgress
import com.eduquiz.pro.data.repository.QuizRepository
import com.eduquiz.pro.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val isGuest: Boolean get() = sessionManager.isGuest()

    private val _results = MutableLiveData<List<QuizResult>>(emptyList())
    val results: LiveData<List<QuizResult>> = _results

    private val _summary = MutableLiveData(UserProgress(0, 0, 0, 0, emptyList()))
    val summary: LiveData<UserProgress> = _summary

    init {
        if (!isGuest) {
            viewModelScope.launch {
                quizRepository.getResults().collectLatest { list ->
                    _results.postValue(list)
                    val total = list.size
                    val avg = if (total == 0) 0 else list.map { it.score * 10 }.average().toInt()
                    val best = list.maxOfOrNull { it.score * 10 } ?: 0
                    val courses = list.map { it.courseId }.distinct().size
                    val recent = list.take(7).map { it.score * 10 }.reversed()
                    _summary.postValue(UserProgress(total, avg, best, courses, recent))
                }
            }
        }
    }
}
