package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoursesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(course: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(courses: List<Course>)

    @Query("SELECT * FROM courses_table ORDER BY `title` ASC")
    fun getAllEvents(): LiveData<List<Course>>

    @Query("DELETE FROM  courses_table")
    fun clear()

}