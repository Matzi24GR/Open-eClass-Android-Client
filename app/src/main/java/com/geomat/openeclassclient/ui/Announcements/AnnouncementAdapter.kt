package com.geomat.openeclassclient.ui.Announcements

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geomat.openeclassclient.databinding.AnnouncementListItemBinding
import com.geomat.openeclassclient.databinding.BottomSheetAnnouncementFullBinding
import com.geomat.openeclassclient.domain.Announcement
import com.geomat.openeclassclient.repository.AnnouncementRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import javax.inject.Inject

class AnnouncementAdapter(private val itemClick: (Announcement) -> Unit, private val itemSeen: (Announcement) -> Unit) : ListAdapter<Announcement, AnnouncementAdapter.ViewHolder>(AnnouncementDiffCallback()) {

    //TODO handle images in announcement content

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, itemClick, itemSeen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: AnnouncementListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(announcement: Announcement, itemClick: (Announcement) -> Unit, itemSeen: (Announcement) -> Unit) {
            with(binding) {
                if (announcement.courseName.isNullOrBlank()) {
                    courseNameText.text = "System"
                } else {
                    courseNameText.text = announcement.courseName
                }
                dateText.text = SimpleDateFormat.getDateTimeInstance().format(announcement.date)
                titleText.text = announcement.title
                descriptionText.maxLines = 5
                descriptionText.text = announcement.description.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT).trim()

                layout.setOnClickListener { itemClick(announcement) }
                descriptionText.setOnClickListener { itemClick(announcement) }

                descriptionText.post(Runnable {
                    val maxLines = 5
                    if (descriptionText.lineCount > maxLines) {
                        //readMoreText.visibility = View.VISIBLE
                        val start = descriptionText.layout.getLineStart(0)
                        val end = descriptionText.layout.getLineEnd(maxLines-1)
                        val string = descriptionText.text.substring(start, end-3)+"<b>...</b>".parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)
                        descriptionText.text = string
                    } else {
                        readMoreText.visibility = View.INVISIBLE
                    }
                })

                if (!announcement.isRead) {
                    newTag.visibility = View.VISIBLE
                    announcement.isRead = true
                    itemSeen(announcement)
                } else {
                    newTag.visibility = View.INVISIBLE
                }
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