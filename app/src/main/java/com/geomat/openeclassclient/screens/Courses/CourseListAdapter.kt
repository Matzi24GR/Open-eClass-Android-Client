package com.geomat.openeclassclient.screens.Courses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.Course

class CourseListAdapter : ListAdapter<Course, CourseListAdapter.ViewHolder>(CourseDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return CourseListAdapter.ViewHolder.from(
            parent
        )
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val titleText = itemView.findViewById<TextView>(R.id.title_text_view)
        private val codeText = itemView.findViewById<TextView>(R.id.code_text_view)
        private val descText = itemView.findViewById<TextView>(R.id.desc_text_view)

        fun bind(course: Course) {
            if (course.desc.isBlank()) {
                descText.visibility = View.GONE
            } else {
                descText.visibility = View.VISIBLE
            }

            titleText.text = course.title
            codeText.text = course.id
            descText.text = course.desc
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.course_list_item, parent, false)

                return ViewHolder(
                    view
                )
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