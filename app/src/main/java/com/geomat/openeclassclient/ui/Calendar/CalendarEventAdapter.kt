package com.geomat.openeclassclient.ui.Calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.databinding.CalendarListItemBinding
import com.geomat.openeclassclient.domain.CalendarEvent
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*

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
                if (calendarEvent.start == calendarEvent.end){
                    textTime.text = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(calendarEvent.start)
                } else {
                    val start = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(calendarEvent.start)
                    val end = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(calendarEvent.end)
                    textTime.text = start + "\n" + end
                }

                if (calendarEvent.content.isBlank()) textContent.visibility = View.GONE
                else textContent.visibility = View.VISIBLE

                //.replaceFirst(Regex("^(.*?): "),"") To get rid of course name at start of title
                textDayOfWeek.text = SimpleDateFormat("EEE").format(calendarEvent.start)
                textDayOfMonth.text = SimpleDateFormat("dd").format(calendarEvent.start)
                textMonthYear.text = SimpleDateFormat("MMM\nYYYY").format(calendarEvent.start)
                textTitle.text = calendarEvent.title
                textContent.text = HtmlCompat.fromHtml(calendarEvent.content, HtmlCompat.FROM_HTML_MODE_COMPACT).trimEnd()
                textEventGroup.text = calendarEvent.event_group
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