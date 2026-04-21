// FILE: app/src/main/java/com/eduquiz/pro/ui/home/CourseAdapter.kt
package com.eduquiz.pro.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eduquiz.pro.data.model.Course
import com.eduquiz.pro.databinding.ItemCourseBinding

class CourseAdapter(
    private val onClick: (Course) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourseViewHolder(private val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.tvIcon.text = course.icon
            binding.tvTitle.text = course.title
            binding.tvDescription.text = course.description
            binding.root.setOnClickListener { onClick(course) }
        }
    }

    object Diff : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem == newItem
    }
}
