// FILE: app/src/main/java/com/eduquiz/pro/ui/results/ResultViewModel.kt
package com.eduquiz.pro.ui.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    val score: Int = savedStateHandle["score"] ?: 0
    val total: Int = savedStateHandle["total"] ?: 10
    val correct: Int = savedStateHandle["correct"] ?: 0
    val wrong: Int = savedStateHandle["wrong"] ?: 0
    val timeout: Int = savedStateHandle["timeout"] ?: 0
    val courseId: Int = savedStateHandle["courseId"] ?: 1
    val courseTitle: String = savedStateHandle["courseTitle"] ?: ""
    val difficulty: String = savedStateHandle["difficulty"] ?: "EASY"
    val reviews: ArrayList<String> = savedStateHandle["reviews"] ?: arrayListOf()

    val percentage: Int get() = if (total == 0) 0 else (score * 100 / total)

    val isPass: Boolean get() = percentage >= 60

    val motivationalMessageRes: Int
        get() = when {
            percentage >= 90 -> com.eduquiz.pro.R.string.msg_outstanding
            percentage >= 70 -> com.eduquiz.pro.R.string.msg_great_job
            percentage >= 50 -> com.eduquiz.pro.R.string.msg_good_effort
            else -> com.eduquiz.pro.R.string.msg_dont_give_up
        }
}
