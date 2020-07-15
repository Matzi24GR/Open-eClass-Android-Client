package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnnouncementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(announcement: Announcement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(announcements: List<Announcement>)

    @Query("SELECT * FROM announcements_table ORDER BY `date` DESC")
    fun getAllAnnouncements(): LiveData<List<Announcement>>

    @Query("DELETE FROM  announcements_table")
    fun clear()

}