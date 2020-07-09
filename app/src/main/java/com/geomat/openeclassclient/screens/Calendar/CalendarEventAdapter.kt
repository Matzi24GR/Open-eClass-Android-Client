package com.geomat.openeclassclient.screens.Calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.CalendarEvent

class CalendarEventAdapter(var data: List<CalendarEvent>) : RecyclerView.Adapter<CalendarEventAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return CalendarEventAdapter.ViewHolder.from(
            parent
        )
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val textView1: TextView = itemView.findViewById(R.id.textID)
        private val textView2: TextView = itemView.findViewById(R.id.textTitle)
        private val textView3: TextView = itemView.findViewById(R.id.textStart)
        private val textView4: TextView = itemView.findViewById(R.id.textEnd)
        private val textView5: TextView = itemView.findViewById(R.id.textContent)
        private val textView6: TextView = itemView.findViewById(R.id.textEventGroup)
        private val textView7: TextView = itemView.findViewById(R.id.textClass)
        private val textView10: TextView = itemView.findViewById(R.id.textEventType)
        private val textView8: TextView = itemView.findViewById(R.id.textCourse)
        private val textView9: TextView = itemView.findViewById(R.id.textUrl)

        fun bind(calendarEvent: CalendarEvent) {
            textView1.text = calendarEvent.id.toString()
            textView2.text = calendarEvent.title
            textView3.text = calendarEvent.start.toString()
            textView4.text = calendarEvent.end.toString()
            textView5.text = calendarEvent.content
            textView6.text = calendarEvent.event_group
            textView7.text = calendarEvent.Class
            textView8.text = calendarEvent.courseCode
            textView9.text = calendarEvent.url
            textView10.text = calendarEvent.event_type

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.calendar_list_item, parent, false)

                return ViewHolder(
                    view
                )
            }
        }
    }
}