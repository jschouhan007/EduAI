// FILE: app/src/main/java/com/eduquiz/pro/ui/home/HomeFragment.kt
package com.eduquiz.pro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eduquiz.pro.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = CourseAdapter { course ->
            val directions = HomeFragmentDirections.actionHomeToDifficulty(course.id, course.title, course.icon)
            findNavController().navigate(directions)
        }
        binding.recyclerCourses.adapter = adapter

        binding.tvGreeting.text = getString(com.eduquiz.pro.R.string.hello_user, viewModel.greetingName)

        binding.etSearch.doAfterTextChanged { viewModel.search(it?.toString().orEmpty()) }

        viewModel.courses.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
