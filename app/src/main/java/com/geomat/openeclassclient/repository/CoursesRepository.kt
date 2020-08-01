package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.CoursesDao
import com.geomat.openeclassclient.database.asDomainModel
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.EclassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

class CoursesRepository(private val coursesDao: CoursesDao ) {

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
            val courses = EclassApi.MobileApi.getCourses(token).await()
            coursesDao.insertAll(courses.asDatabaseModel())
        }
    }
}