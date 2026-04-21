// FILE: app/src/main/java/com/eduquiz/pro/ui/progress/ProgressFragment.kt
package com.eduquiz.pro.ui.progress

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eduquiz.pro.databinding.FragmentProgressBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels

@AndroidEntryPoint
class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProgressViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (viewModel.isGuest) {
            binding.groupGuest.visibility = View.VISIBLE
            binding.groupUser.visibility = View.GONE
            return
        }

        binding.groupGuest.visibility = View.GONE
        binding.groupUser.visibility = View.VISIBLE

        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            binding.tvTotalQuizzes.text = summary.totalQuizzes.toString()
            binding.tvAverageScore.text = "${summary.averageScore}%"
            binding.tvBestScore.text = "${summary.bestScore}%"
            binding.tvCoursesAttempted.text = summary.coursesAttempted.toString()
            drawChart(summary.recentScores)
        }

        viewModel.results.observe(viewLifecycleOwner) { list ->
            binding.tvHistory.text = list.joinToString("\n\n") {
                "${it.courseTitle} • ${it.difficulty.name} • ${it.score}/${it.totalQuestions} • ${java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(java.util.Date(it.timestamp))}"
            }
        }
    }

    private fun drawChart(scores: List<Int>) {
        if (scores.isEmpty() || binding.chartView.width == 0 || binding.chartView.height == 0) return
        val width = binding.chartView.width
        val height = binding.chartView.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0") }
        val gap = width / (scores.size * 2f)
        val barWidth = gap
        scores.forEachIndexed { i, score ->
            val left = gap + i * (barWidth + gap)
            val barHeight = (height * (score / 100f))
            canvas.drawRect(left, height - barHeight, left + barWidth, height.toFloat(), paint)
        }
        binding.chartView.setImageBitmap(bitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
