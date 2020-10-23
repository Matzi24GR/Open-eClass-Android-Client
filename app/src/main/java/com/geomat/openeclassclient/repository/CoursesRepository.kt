package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.CoursesDao
import com.geomat.openeclassclient.database.DatabaseCourse
import com.geomat.openeclassclient.database.DatabaseFeedUrl
import com.geomat.openeclassclient.database.asDomainModel
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.EclassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class CoursesRepository @Inject constructor(private val coursesDao: CoursesDao) {

    val allCourses: LiveData<List<Course>> = Transformations.map(coursesDao.getAllCourses()){
        it.asDomainModel()
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            coursesDao.clear()
        }
    }

    suspend fun refreshData(token: String) {

        withContext(Dispatchers.IO) {
            try {
                //Get Courses
                val coursesResponse = EclassApi.MobileApi.getCourses(token).await()
                val courses = coursesResponse.asDatabaseModel()
                //Insert Courses
                val result = coursesDao.insertAll(courses)
                //Update courses that failed to insert
                val toUpdate = mutableListOf<DatabaseCourse>()
                for (i in result.indices) {
                    if (result[i] == -1L) {
                        toUpdate.add(courses[i])
                    }
                }
                coursesDao.updateAll(toUpdate)
                //Remove Deleted Courses
                val toRetain = courses.map {it.id}
                coursesDao.clearNotInList(toRetain)
                //Set Announcement Rss Feed Urls
                setFeedUrls(token)
            } catch (e: Exception) {
                Timber.i(e)
            }
        }
    }

    private suspend fun setFeedUrls(token: String) {
        //Refresh Courses if none exist
        if (coursesDao.getNumberOfCourses() == 0) {
            refreshData(token)
        }
        //Set feed for each one
        val coursesWithNoFeedUrl = coursesDao.getCoursesWithNoFeedUrl()
        coursesWithNoFeedUrl.forEach {
            val url = getRssUrlForCourse(token, it.asDomainModel())
            if (url != null) {
                coursesDao.insertFeedUrl(DatabaseFeedUrl(url, it.id))
            }
        }
    }

    private suspend fun getRssUrlForCourse(token: String, course: Course): String? {
        //Get announcement page
        val page = EclassApi.HtmlParser.getAnnouncementPage("PHPSESSID=$token", course.id).await()
        val document = Jsoup.parse(page)
        //Parse url
        val url = document.select("a[href*=/modules/announcements/rss]").attr("href")
        if (url.isNotBlank()) {
            return url
        }
        return null
    }
}