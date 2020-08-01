package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnnouncementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(databaseAnnouncement: DatabaseAnnouncement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(databaseAnnouncements: List<DatabaseAnnouncement>)

    @Query("SELECT * FROM announcements_table ORDER BY `date` DESC")
    fun getAllAnnouncements(): LiveData<List<DatabaseAnnouncement>>

    @Query("DELETE FROM  announcements_table")
    fun clear()

}