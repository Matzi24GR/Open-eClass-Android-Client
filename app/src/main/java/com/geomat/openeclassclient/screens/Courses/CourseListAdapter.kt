package com.geomat.openeclassclient.screens.Courses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.database.Course
import com.geomat.openeclassclient.databinding.CourseListItemBinding

class CourseListAdapter : ListAdapter<Course, CourseListAdapter.ViewHolder>(CourseDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: CourseListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(course: Course) {
            with(binding) {
                if (course.desc.isBlank()) {
                    descTextView.visibility = View.GONE
                } else {
                    descTextView.visibility = View.VISIBLE
                }

                titleTextView.text = course.title
                codeTextView.text = course.id
                descTextView.text = course.desc
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val itemBinding = CourseListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(itemBinding)
            }
        }
    }

    class CourseDiffCallback: DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
}