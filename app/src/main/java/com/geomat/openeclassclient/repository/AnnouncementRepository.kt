package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.database.DatabaseFeedUrl
import com.geomat.openeclassclient.database.asDomainModel
import com.geomat.openeclassclient.domain.Announcement
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.EclassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.await
import timber.log.Timber
import java.lang.Exception

class AnnouncementRepository(database: EClassDatabase) {

    private val courseDao = database.coursesDao
    private val announcementDao = database.announcementDao

    val allAnnouncements: LiveData<List<Announcement>> = Transformations.map(announcementDao.getAllAnnouncements()){
        it.asDomainModel()
    }

    suspend fun updateAllAnnouncements() {
        withContext(Dispatchers.IO) {
            val feedUrls = courseDao.getAllFeedUrls() as MutableList
            feedUrls.add("/rss.php")        //System Announcements Url
            feedUrls.forEach { currentFeed ->
                try {
                    val announcements = EclassApi.MobileApi.getRssFeed(currentFeed).await()
                    announcementDao.insertAll(announcements.asDatabaseModel())
                } catch (e: Exception) {
                    Timber.i(e)
                }
            }
        }
    }

    suspend fun fillInFeedUrls(token: String) {
        if (courseDao.getNumberOfCourses() == 0) {
            CoursesRepository(courseDao).refreshData(token)
        }
        val allCourses = courseDao.getCoursesWithNoFeedUrl()
        allCourses.forEach {
            setRssUrlForCourse(token, it.id)
        }
    }

    private suspend fun setRssUrlForCourse(token: String, course: String) {
        withContext(Dispatchers.IO) {
            try {
                val page = EclassApi.HtmlParser.getAnnouncementPage("PHPSESSID=$token", course).await()
                val document = Jsoup.parse(page)
                val url = document.select("a[href*=/modules/announcements/rss]").attr("href")
                if (url.isNotBlank()) {
                    courseDao.insertFeedUrl(DatabaseFeedUrl(url, course))
                }
            } catch (e: Exception) {
                Timber.i(e)
            }

        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            announcementDao.clear()
        }
    }

}