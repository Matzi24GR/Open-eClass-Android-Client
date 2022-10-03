package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.CoursesDao
import com.geomat.openeclassclient.database.DatabaseCourse
import com.geomat.openeclassclient.database.DatabaseFeedUrl
import com.geomat.openeclassclient.database.asDomainModel
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.network.DataTransferObjects.CoursePageResponse
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.DataTransferObjects.toSingleSeparatedString
import com.geomat.openeclassclient.network.OpenEclassService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class CoursesRepository @Inject constructor(private val coursesDao: CoursesDao, private val credentialsRepository: CredentialsRepository, private val openEclassService: OpenEclassService) {

    val allCourses: LiveData<List<Course>> = Transformations.map(coursesDao.getAllCourses()){
        it.asDomainModel()
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            coursesDao.clear()
        }
    }

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            try {
                //Get Courses
                val coursesResponse = openEclassService.getCourses().await()
                val courses = coursesResponse.asDatabaseModel()
                //Insert Courses
                val result = coursesDao.insertAll(courses)
                //Remove Deleted Courses
                val toRetain = courses.map {it.id}
                coursesDao.clearNotInList(toRetain)
                //Set Announcement Rss Feed Urls
                setFeedUrls()
            } catch (e: Exception) {
                Timber.i(e)
            }
        }
    }

    fun getCourseFlow(course: Course) : Flow<Course?> {
        return coursesDao.getCourseWithId(courseId = course.id).map { it?.asDomainModel() }
    }

    suspend fun updateCourseDetails(course: Course) {
        val host = credentialsRepository.credentialsFlow.first().serverUrl
        withContext(Dispatchers.IO) {
            try {
                val coursePageResponse = CoursePageResponse(openEclassService.getCoursePage(course.id).await())
                val toolsResponse = openEclassService.getTools(courseId = course.id).await()
                val tools = toolsResponse.toSingleSeparatedString()
                val databaseCourse = DatabaseCourse(id = course.id, title = course.title, desc = coursePageResponse.desc+"\n"+coursePageResponse.moreInfo, imageUrl = "https://" + host + coursePageResponse.imageUrl, tools = tools)
                coursesDao.update(databaseCourse)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private suspend fun setFeedUrls() {
        //Refresh Courses if none exist
        if (coursesDao.getNumberOfCourses() == 0) {
            refreshData()
        }
        //Set feed for each one
        val coursesWithNoFeedUrl = coursesDao.getCoursesWithNoFeedUrl()
        coursesWithNoFeedUrl.forEach {
            val url = getRssUrlForCourse(it.asDomainModel())
            if (url != null) {
                coursesDao.insertFeedUrl(DatabaseFeedUrl(url, it.id))
            }
        }
    }

    private suspend fun getRssUrlForCourse(course: Course): String? {
        //Get announcement page
        val page = openEclassService.getAnnouncementPage(course.id).await()
        val document = Jsoup.parse(page)
        //Parse url
        val url = document.select("a[href*=/modules/announcements/rss]").attr("href")
        if (url.isNotBlank()) {
            return url
        }
        return null
    }
}