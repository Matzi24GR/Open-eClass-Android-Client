package com.geomat.openeclassclient.repository

import android.net.Uri
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LiveData
import com.geomat.openeclassclient.database.Announcement
import com.geomat.openeclassclient.database.CalendarEvent
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.network.eClassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementRepository(database: EClassDatabase) {

    private val courseDao = database.coursesDao
    private val announcementDao = database.announcementDao

    val allAnnouncements: LiveData<List<Announcement>> = announcementDao.getAllAnnouncements()

    suspend fun updateAllAnnouncements() {
        withContext(Dispatchers.IO) {
            val allCourses = courseDao.getAllCoursesNonLive()
            val feedUrls = mutableListOf<String>()
            feedUrls.add("/rss.php")        //System Announcements Url
            allCourses.forEach {
                Timber.i(it.announcementFeedUrl)
                feedUrls.add(it.announcementFeedUrl)
            }
            feedUrls.forEach { currentFeed ->
                if (currentFeed.isNotBlank()) {
                    try {
                        val result = eClassApi.MobileApi.getRssFeed(currentFeed).execute()
                        if (result.isSuccessful) {
                            val announcements = result.body()?.Channel?.announcementList
                            announcements?.forEach {curentAnnouncement ->
                                var announcement = Announcement("","","","","",0)
                                val Uri = Uri.parse(curentAnnouncement.link.parseAsHtml().toString())
                                val id = Uri.getQueryParameter("aid")
                                if (id.isNullOrEmpty()) { // That means its a normal announcement
                                    val an_id = Uri.getQueryParameter("an_id")
                                    val c_id  = Uri.getQueryParameter("course")
                                    announcement.id = an_id.toString()
                                    announcement.courseId = c_id
                                } else {                  // System announcement
                                    val aid = Uri.getQueryParameter("aid")
                                    announcement.id = "s$aid"   //add s to the start of id so it is different compared to course announcements
                                    announcement.courseId = null
                                }
                                val dateFormat = SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                                val date = dateFormat.parse(curentAnnouncement.pubDate)
                                announcement.title = curentAnnouncement.title
                                announcement.link = curentAnnouncement.link
                                announcement.description = curentAnnouncement.description.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
                                announcement.date = date.time

                               announcementDao.insert(announcement)

                            }
                        }
                    } catch (cause: Throwable) {
                    }
                }
            }
        }
    }

    suspend fun fillInFeedUrls(token: String) {
        val allCourses = courseDao.getAllCoursesNonLive()
        allCourses.forEach {
            if (it.announcementFeedUrl.isBlank()) {
                setRssUrlForCourse(token, it.id)
            }
        }
    }

    suspend fun setRssUrlForCourse(token: String, course: String) {
        withContext(Dispatchers.IO) {
            try {
                val result = eClassApi.HtmlParser.getAnnouncementPage("PHPSESSID=$token", course).execute()
                if (result.isSuccessful) {
                    val document = Jsoup.parse(result.body())
                    val url = document.select("a[href*=/modules/announcements/rss]").attr("href")

                    if (url.isNotBlank()) {
                        courseDao.setAnnouncementFeedUrl(url, course)
                    }
                }
            } catch (cause: Throwable) {
            }
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            announcementDao.clear()
        }
    }

}