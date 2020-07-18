package com.geomat.openeclassclient.screens.Calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.database.CalendarEvent
import com.geomat.openeclassclient.databinding.CalendarListItemBinding
import java.text.SimpleDateFormat

class CalendarEventAdapter : ListAdapter<CalendarEvent, CalendarEventAdapter.ViewHolder>(CalendarEventDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: CalendarListItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(calendarEvent: CalendarEvent) {
            with(binding) {
                textID.text = calendarEvent.id.toString()
                textTitle.text = calendarEvent.title
                textStart.text = SimpleDateFormat.getDateTimeInstance().format(calendarEvent.start)
                textEnd.text = SimpleDateFormat.getDateTimeInstance().format(calendarEvent.end)
                textContent.text = HtmlCompat.fromHtml(calendarEvent.content, HtmlCompat.FROM_HTML_MODE_COMPACT)
                textEventGroup.text = calendarEvent.event_group
                textClass.text = calendarEvent.Class
                textCourse.text = calendarEvent.courseCode
                textUrl.text = calendarEvent.url
                textEventType.text = calendarEvent.event_type
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val itemBinding = CalendarListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(itemBinding)
            }
        }
    }

    class CalendarEventDiffCallback: DiffUtil.ItemCallback<CalendarEvent>() {
        override fun areItemsTheSame(oldItem: CalendarEvent, newItem: CalendarEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CalendarEvent, newItem: CalendarEvent): Boolean {
            return oldItem == newItem
        }
    }
}