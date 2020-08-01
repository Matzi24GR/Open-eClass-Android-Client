package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CoursesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(course: DatabaseCourse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(courses: List<DatabaseCourse>)

    @Query("SELECT * FROM courses_table ORDER BY `title` ASC")
    fun getAllCourses(): LiveData<List<DatabaseCourse>>

    @Query("SELECT * FROM courses_table ORDER BY `title` ASC")
    fun getAllCoursesNonLive(): List<DatabaseCourse>

    @Query("DELETE FROM  courses_table")
    fun clear()

    @Query("SELECT COUNT(*) FROM courses_table")
    fun getNumberOfCourses(): Int

    @Query("UPDATE courses_table SET announcementFeedUrl = :url WHERE id = :courseId")
    fun setAnnouncementFeedUrl(url: String, courseId: String)

}