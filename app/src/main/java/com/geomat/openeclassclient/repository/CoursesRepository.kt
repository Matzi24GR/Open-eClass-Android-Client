package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import com.geomat.openeclassclient.database.Course
import com.geomat.openeclassclient.database.CoursesDao
import com.geomat.openeclassclient.network.EclassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoursesRepository(private val coursesDao: CoursesDao ) {

    val allCourses: LiveData<List<Course>> = coursesDao.getAllCourses()

    fun insertAll(courses: List<Course>) {
        coursesDao.insertAll(courses)
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            coursesDao.clear()
        }
    }

    suspend fun refreshData(token: String) {

        withContext(Dispatchers.IO) {
            try {
                val result = EclassApi.MobileApi.getCourses(token).execute()
                if (result.isSuccessful) {
                    val responseList = result.body()?.courseGroup?.courseList
                    val dbList = mutableListOf<Course>()

                    if (responseList!!.isNotEmpty()) {
                        for (i in responseList.indices) {
                            with(responseList[i]){
                                dbList.add(
                                    Course(this.code,this.title,this.description,"")
                                )
                            }
                        }
                    }
                    insertAll(dbList)
                }
            } catch (cause: Throwable) {
            }
        }
    }
}