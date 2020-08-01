package com.geomat.openeclassclient.network.DataTransferObjects

import android.net.Uri
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import com.geomat.openeclassclient.database.DatabaseAnnouncement
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import java.text.SimpleDateFormat
import java.util.*

@Xml(name = "rss")
class RssResponse {

    @Attribute(name="version")
    var version: String = ""

    @Element(name = "channel")
    lateinit var channel: Channel
}

@Xml(name = "channel")
data class Channel (

    @Element(name = "atom:link")
    var atomLink: AtomLink,

    @PropertyElement
    var title: String = "",

    @PropertyElement
    var link: String = "",

    @PropertyElement
    var description: String = "",

    @PropertyElement
    var lastBuildDate: String = "",

    @PropertyElement
    var language: String = "",

    @Element(name="item")
    val netWorkAnnouncementList: List<NetWorkAnnouncement>?
)

@Xml(name = "atom:link")
class AtomLink {
    @Attribute
    var href: String = ""

    @Attribute
    var rel: String = ""

    @Attribute
    var type: String = ""
}

@Xml(name = "item")
class NetWorkAnnouncement {

    @PropertyElement
    var title: String = ""

    @PropertyElement
    var link: String = ""

    @PropertyElement
    var description: String = ""

    @PropertyElement
    var pubDate: String = ""

}

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