package com.geomat.openeclassclient.network.DataTransferObjects

import android.net.Uri
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import com.geomat.openeclassclient.database.DatabaseAnnouncement
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.text.SimpleDateFormat
import java.util.*

@kotlinx.serialization.Serializable
@XmlSerialName("rss","","")
data class RssResponse (
    val version: String,
    val channel: Channel
)

@kotlinx.serialization.Serializable
@XmlSerialName("channel","","")
data class Channel (
    @XmlElement(true) val atomLink: AtomLink,
    @XmlElement(true) val title: String,
    @XmlElement(true) val link: String,
    @XmlElement(true) val description: String,
    @XmlElement(true) val lastBuildDate: String,
    @XmlElement(true) val language: String,
    @XmlElement(true) val netWorkAnnouncementList: List<NetWorkAnnouncement>?
)

@kotlinx.serialization.Serializable
@XmlSerialName("link","http://www.w3.org/2005/Atom","")
class AtomLink (
    val href: String,
    val rel: String,
    val type: String
)

@kotlinx.serialization.Serializable
@XmlSerialName("item","","")
class NetWorkAnnouncement (
    @XmlElement(true) val title: String,
    @XmlElement(true) val link: String,
    @XmlElement(true) val description: String,
    @XmlElement(true) val pubDate: String,
    @XmlElement(true) val guid: String,
)

fun RssResponse.asDatabaseModel(): List<DatabaseAnnouncement> {
    if (channel.netWorkAnnouncementList.isNullOrEmpty()) {
        return emptyList()
    }
    return channel.netWorkAnnouncementList!!.map {
        val announcement = DatabaseAnnouncement("","","","","",0)
        val uri = Uri.parse(it.link.parseAsHtml().toString())
        val id = uri.getQueryParameter("aid")
        if (id.isNullOrEmpty()) { // That means its a normal announcement
            val anId = uri.getQueryParameter("an_id")
            val cId  = uri.getQueryParameter("course")
            announcement.id = anId.toString()
            announcement.courseId = cId
        } else {                  // System announcement
            val aid = uri.getQueryParameter("aid")
            announcement.id = "s$aid"   //add s to the start of id so it is different compared to course announcements
            announcement.courseId = null
        }

        val dateFormat = SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        val date = dateFormat.parse(it.pubDate)
        announcement.title = it.title
        announcement.link = it.link
        announcement.description = it.description.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
        announcement.date = date.time

        return@map announcement
    }
}