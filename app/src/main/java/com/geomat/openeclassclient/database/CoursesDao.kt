package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CoursesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(course: DatabaseCourse)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(courses: List<DatabaseCourse>)

    @Query("SELECT * FROM courses_table ORDER BY `title` ASC")
    fun getAllCourses(): LiveData<List<DatabaseCourse>>

    @Query("SELECT * FROM courses_table ORDER BY `title` ASC")
    fun getAllCoursesNonLive(): List<DatabaseCourse>

    @Query("DELETE FROM courses_table")
    fun clear()

    @Query("SELECT COUNT(*) FROM courses_table")
    fun getNumberOfCourses(): Int

    @Query("SELECT * FROM courses_table c WHERE NOT EXISTS ( SELECT * FROM feed_urls_table f WHERE c.id = f.courseId) ORDER BY `title` ASC")
    fun getCoursesWithNoFeedUrl(): List<DatabaseCourse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeedUrl(urlDatabase: DatabaseFeedUrl)

    @Query("SELECT announcementFeedUrl FROM feed_urls_table")
    fun getAllFeedUrls(): List<String>

}