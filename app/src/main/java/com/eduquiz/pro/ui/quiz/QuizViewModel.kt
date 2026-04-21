// FILE: app/src/main/java/com/eduquiz/pro/ui/quiz/QuizViewModel.kt
package com.eduquiz.pro.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduquiz.pro.data.model.Difficulty
import com.eduquiz.pro.data.model.Question
import com.eduquiz.pro.data.model.QuizResult
import com.eduquiz.pro.data.repository.QuizRepository
import com.eduquiz.pro.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class QuestionReview(val question: String, val selected: String, val correct: String, val explanation: String)

    data class QuizUiState(
        val loading: Boolean = true,
        val completed: Boolean = false,
        val questionIndex: Int = 0,
        val total: Int = 10,
        val timeLeftSec: Int = 30,
        val selectedOption: String? = null,
        val showExplanation: Boolean = false,
        val timedOut: Boolean = false,
        val questions: List<Question> = emptyList(),
        val reviews: List<QuestionReview> = emptyList(),
        val correctCount: Int = 0,
        val wrongCount: Int = 0,
        val timeoutCount: Int = 0
    ) {
        val currentQuestion: Question? get() = questions.getOrNull(questionIndex)
    }

    private val _state = MutableLiveData(QuizUiState())
    val state: LiveData<QuizUiState> = _state

    private var timerJob: Job? = null

    private val courseId: Int = savedStateHandle["courseId"] ?: 1
    private val courseTitle: String = savedStateHandle["courseTitle"] ?: ""
    private val difficulty: Difficulty = Difficulty.valueOf(savedStateHandle["difficulty"] ?: "EASY")

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val questions = quizRepository.getQuizQuestions(courseId, difficulty)
            _state.value = _state.value?.copy(loading = false, questions = questions, total = questions.size)
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var t = 30
            while (t > 0 && _state.value?.showExplanation == false) {
                _state.postValue(_state.value?.copy(timeLeftSec = t))
                delay(1000)
                t--
            }
            if ((_state.value?.showExplanation == false) && !_state.value!!.completed) {
                onOptionSelected("", timedOut = true)
            }
        }
    }

    fun onOptionSelected(option: String, timedOut: Boolean = false) {
        val s = _state.value ?: return
        if (s.showExplanation || s.completed) return
        val q = s.currentQuestion ?: return

        val isCorrect = option == q.correctOption
        val newReviews = s.reviews + QuestionReview(
            question = q.questionText,
            selected = if (timedOut) "TIMEOUT" else option,
            correct = q.correctOption,
            explanation = q.explanation
        )

        _state.value = s.copy(
            selectedOption = option,
            showExplanation = true,
            timedOut = timedOut,
            correctCount = s.correctCount + if (isCorrect) 1 else 0,
            wrongCount = s.wrongCount + if (!isCorrect && !timedOut) 1 else 0,
            timeoutCount = s.timeoutCount + if (timedOut) 1 else 0,
            reviews = newReviews
        )

        timerJob?.cancel()
        viewModelScope.launch {
            delay(2000)
            nextQuestion()
        }
    }

    fun nextQuestion() {
        val s = _state.value ?: return
        if (!s.showExplanation) return

        if (s.questionIndex + 1 >= s.total) {
            _state.value = s.copy(completed = true)
            saveResultIfAllowed()
            return
        }

        _state.value = s.copy(
            questionIndex = s.questionIndex + 1,
            timeLeftSec = 30,
            selectedOption = null,
            showExplanation = false,
            timedOut = false
        )
        startTimer()
    }

    private fun saveResultIfAllowed() {
        if (sessionManager.isGuest()) return
        val s = _state.value ?: return
        viewModelScope.launch {
            quizRepository.saveResult(
                QuizResult(
                    courseId = courseId,
                    courseTitle = courseTitle,
                    difficulty = difficulty,
                    score = s.correctCount,
                    totalQuestions = s.total,
                    correctCount = s.correctCount,
                    wrongCount = s.wrongCount,
                    timeoutCount = s.timeoutCount
                )
            )
        }
    }
}
