package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AnnouncementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(databaseAnnouncement: DatabaseAnnouncement)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(databaseAnnouncements: List<DatabaseAnnouncement>)

    @Query("SELECT * FROM announcements_table ORDER BY `date` DESC")
    fun getAllAnnouncements(): LiveData<List<DatabaseAnnouncement>>

    @Query("SELECT a.* , c.title courseName, r.isRead FROM announcements_table a left join courses_table c on a.courseId = c.id natural join announcement_read_table r ORDER BY `date` DESC")
    fun getAllAnnouncementsWithCourseNames(): LiveData<List<DatabaseAnnouncementWithCourseName>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAllReadStatus(readStatus: List<DatabaseAnnouncementReadStatus>)

    @Query("UPDATE announcement_read_table SET isRead = :status WHERE id = :id")
    fun updateReadStatus(id: String, status: Boolean)

    @Query("SELECT COUNT(id) FROM announcement_read_table WHERE isRead = 0")
    fun getUnreadCount(): LiveData<Int>

    @Query("DELETE FROM  announcements_table")
    fun clear()

}