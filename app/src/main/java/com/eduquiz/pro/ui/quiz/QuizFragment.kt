// FILE: app/src/main/java/com/eduquiz/pro/ui/quiz/QuizFragment.kt
package com.eduquiz.pro.ui.quiz

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eduquiz.pro.R
import com.eduquiz.pro.data.model.Question
import com.eduquiz.pro.databinding.FragmentQuizBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()
    private val args: QuizFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupBackPress()

        val click: (String) -> Unit = { viewModel.onOptionSelected(it) }
        binding.optionA.setOnClickListener { click("A") }
        binding.optionB.setOnClickListener { click("B") }
        binding.optionC.setOnClickListener { click("C") }
        binding.optionD.setOnClickListener { click("D") }
        binding.btnNext.setOnClickListener { viewModel.nextQuestion() }

        viewModel.state.observe(viewLifecycleOwner) { s ->
            binding.progressLinear.max = if (s.total == 0) 10 else s.total
            binding.progressLinear.progress = s.questionIndex + 1
            binding.tvQuestionCount.text = getString(R.string.question_x_of_y, s.questionIndex + 1, s.total)
            binding.tvTimer.text = getString(R.string.timer_seconds, s.timeLeftSec)
            binding.btnNext.visibility = if (s.showExplanation) View.VISIBLE else View.GONE

            if (s.completed) {
                val reviews = ArrayList(s.reviews.map { "Q: ${it.question}\nSelected: ${it.selected}\nCorrect: ${it.correct}\nWhy: ${it.explanation}" })
                val directions = QuizFragmentDirections.actionQuizToResult(
                    score = s.correctCount,
                    total = s.total,
                    correct = s.correctCount,
                    wrong = s.wrongCount,
                    timeout = s.timeoutCount,
                    courseId = args.courseId,
                    courseTitle = args.courseTitle,
                    difficulty = args.difficulty,
                    reviews = reviews
                )
                findNavController().navigate(directions)
                return@observe
            }

            renderQuestion(s.currentQuestion)
            if (s.showExplanation) {
                val q = s.currentQuestion
                binding.tvExplanation.visibility = View.VISIBLE
                binding.tvExplanation.text = q?.explanation
                highlightAnswers(q, s.selectedOption, s.timedOut)
            } else {
                binding.tvExplanation.visibility = View.GONE
                resetOptions()
            }
        }
    }

    private fun renderQuestion(question: Question?) {
        if (question == null) return
        binding.tvQuestion.text = question.questionText
        binding.optionA.text = getString(R.string.option_format, "A", question.optionA)
        binding.optionB.text = getString(R.string.option_format, "B", question.optionB)
        binding.optionC.text = getString(R.string.option_format, "C", question.optionC)
        binding.optionD.text = getString(R.string.option_format, "D", question.optionD)
    }

    private fun resetOptions() {
        listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD).forEach {
            it.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            it.isEnabled = true
        }
    }

    private fun highlightAnswers(question: Question?, selected: String?, timedOut: Boolean) {
        val map = mapOf("A" to binding.optionA, "B" to binding.optionB, "C" to binding.optionC, "D" to binding.optionD)
        map.values.forEach { it.isEnabled = false }
        map[question?.correctOption]?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_green))
        if (!timedOut && selected != null && selected != question?.correctOption) {
            map[selected]?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.wrong_red))
        }
    }

    private fun setupBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.quit_quiz_title)
                    .setMessage(R.string.quit_quiz_message)
                    .setPositiveButton(R.string.yes) { _, _ -> findNavController().popBackStack() }
                    .setNegativeButton(R.string.no, null)
                    .show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
