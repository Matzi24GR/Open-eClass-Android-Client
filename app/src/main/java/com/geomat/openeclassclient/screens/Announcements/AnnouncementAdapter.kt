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
import com.geomat.openeclassclient.databinding.AnnouncementListItemBinding
import timber.log.Timber
import java.text.SimpleDateFormat

class AnnouncementAdapter : ListAdapter<Announcement, AnnouncementAdapter.ViewHolder>(AnnouncementDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: AnnouncementListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(announcement: Announcement) {
            with(binding) {
                if (announcement.courseId.isNullOrBlank()) {
                    courseNameText.text = "System"
                } else {
                    courseNameText.text = announcement.courseId
                }
                dateText.text = SimpleDateFormat.getDateTimeInstance().format(announcement.date)
                titleText.text = announcement.title
                descriptionText.text = announcement.description.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val itemBinding = AnnouncementListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(itemBinding)
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