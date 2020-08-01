package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.DatabaseAnnouncement
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.database.asDomainModel
import com.geomat.openeclassclient.domain.Announcement
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.EclassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.await
import timber.log.Timber

class AnnouncementRepository(database: EClassDatabase) {

    private val courseDao = database.coursesDao
    private val announcementDao = database.announcementDao

    val allAnnouncements: LiveData<List<Announcement>> = Transformations.map(announcementDao.getAllAnnouncements()){
        it.asDomainModel()
    }

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
                    val announcements = EclassApi.MobileApi.getRssFeed(currentFeed).await()
                    announcementDao.insertAll(announcements.asDatabaseModel())
                }
            }
        }
    }

    suspend fun fillInFeedUrls(token: String) {
        if (courseDao.getNumberOfCourses() == 0) {
            CoursesRepository(courseDao).refreshData(token)
        }
        val allCourses = courseDao.getAllCoursesNonLive()
        allCourses.forEach {
            if (it.announcementFeedUrl.isBlank()) {
                setRssUrlForCourse(token, it.id)
            }
        }
    }

    private suspend fun setRssUrlForCourse(token: String, course: String) {
        withContext(Dispatchers.IO) {
            val page = EclassApi.HtmlParser.getAnnouncementPage("PHPSESSID=$token", course).await()
            val document = Jsoup.parse(page)
            val url = document.select("a[href*=/modules/announcements/rss]").attr("href")
            if (url.isNotBlank()) {
                courseDao.setAnnouncementFeedUrl(url, course)
            }

        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            announcementDao.clear()
        }
    }

}