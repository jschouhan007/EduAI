// FILE: app/src/main/java/com/eduquiz/pro/ui/results/ResultFragment.kt
package com.eduquiz.pro.ui.results

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eduquiz.pro.R
import com.eduquiz.pro.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResultViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.circularScore.progress = viewModel.percentage
        binding.tvScore.text = getString(R.string.score_x_of_y, viewModel.score, viewModel.total)
        binding.tvPercent.text = getString(R.string.percent_format, viewModel.percentage)
        binding.tvPassFail.text = getString(if (viewModel.isPass) R.string.pass else R.string.fail)

        binding.tvStats.text = getString(
            R.string.result_breakdown,
            viewModel.correct,
            viewModel.wrong,
            viewModel.timeout
        )
        binding.tvMotivation.text = getString(viewModel.motivationalMessageRes)

        binding.btnReviewAnswers.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.review_answers)
                .setMessage(viewModel.reviews.joinToString("\n\n"))
                .setPositiveButton(R.string.ok, null)
                .show()
        }

        binding.btnTryAgain.setOnClickListener {
            val directions = ResultFragmentDirections.actionResultToQuiz(
                courseId = viewModel.courseId,
                courseTitle = viewModel.courseTitle,
                difficulty = viewModel.difficulty
            )
            findNavController().navigate(directions)
        }

        binding.btnGoHome.setOnClickListener {
            findNavController().navigate(ResultFragmentDirections.actionResultToHome())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
