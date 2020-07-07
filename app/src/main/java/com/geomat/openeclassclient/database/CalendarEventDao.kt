package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CalendarEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: CalendarEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<CalendarEvent>)

    @Query("SELECT * FROM calendar_event_table ORDER BY `end` DESC")
    fun getAllEvents(): LiveData<List<CalendarEvent>>

    @Query("DELETE FROM  calendar_event_table")
    fun clear()

}