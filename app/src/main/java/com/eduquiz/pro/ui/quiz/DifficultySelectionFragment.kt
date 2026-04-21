// FILE: app/src/main/java/com/eduquiz/pro/ui/quiz/DifficultySelectionFragment.kt
package com.eduquiz.pro.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eduquiz.pro.databinding.FragmentDifficultyBinding

class DifficultySelectionFragment : Fragment() {

    private var _binding: FragmentDifficultyBinding? = null
    private val binding get() = _binding!!
    private val args: DifficultySelectionFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDifficultyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvCourseIcon.text = args.courseIcon
        binding.tvCourseTitle.text = args.courseTitle

        binding.cardEasy.setOnClickListener { openQuiz("EASY") }
        binding.cardMedium.setOnClickListener { openQuiz("MEDIUM") }
        binding.cardHard.setOnClickListener { openQuiz("HARD") }
    }

    private fun openQuiz(level: String) {
        val directions = DifficultySelectionFragmentDirections.actionDifficultyToQuiz(args.courseId, args.courseTitle, level)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
