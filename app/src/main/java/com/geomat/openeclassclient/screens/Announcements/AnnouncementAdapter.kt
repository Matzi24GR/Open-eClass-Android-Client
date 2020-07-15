package com.geomat.openeclassclient.screens.Announcements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.database.Announcement
import com.geomat.openeclassclient.database.CalendarEvent
import timber.log.Timber
import java.text.SimpleDateFormat

class AnnouncementAdapter : ListAdapter<Announcement, AnnouncementAdapter.ViewHolder>(AnnouncementDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return AnnouncementAdapter.ViewHolder.from(
            parent
        )
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val descText: TextView = itemView.findViewById(R.id.descriptionText)
        private val dateText: TextView = itemView.findViewById(R.id.date_text)
        private val courseText: TextView = itemView.findViewById(R.id.courseNameText)

        fun bind(announcement: Announcement) {
            if (announcement.courseId.isNullOrBlank()) {
                courseText.text = "System"
            } else {
                courseText.text = announcement.courseId
            }
            dateText.text = SimpleDateFormat.getDateTimeInstance().format(announcement.date)
            titleText.text = announcement.title
            descText.text = announcement.description.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.announcement_list_item, parent, false)

                return ViewHolder(
                    view
                )
            }
        }
    }

    class AnnouncementDiffCallback: DiffUtil.ItemCallback<Announcement>() {
        override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
            return oldItem == newItem
        }
    }
}