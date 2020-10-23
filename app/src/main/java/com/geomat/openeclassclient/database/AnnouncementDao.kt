package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AnnouncementDao {

    @Update
    fun updateAll(courses: List<DatabaseAnnouncement>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(announcements: List<DatabaseAnnouncement>): List<Long>

    @Query("SELECT * FROM announcements_table ORDER BY `date` DESC")
    fun getAllAnnouncements(): LiveData<List<DatabaseAnnouncement>>

    @Query("SELECT a.* , c.title courseName, r.isRead FROM announcements_table a left join courses_table c on a.courseId = c.id natural join announcement_read_table r ORDER BY `date` DESC")
    fun getAllAnnouncementsWithCourseNames(): LiveData<List<DatabaseAnnouncementWithCourseName>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllReadStatus(readStatus: List<DatabaseAnnouncementReadStatus>)

    @Update
    fun updateReadStatus(readStatus: DatabaseAnnouncementReadStatus)

    @Query("SELECT COUNT(id) FROM announcement_read_table WHERE isRead = 0")
    fun getUnreadCount(): LiveData<Int>

    @Query("DELETE FROM  announcements_table")
    fun clear()

    @Query("DELETE FROM announcements_table WHERE id NOT IN (:ids)")
    fun clearNotInList(ids: List<String>)

}