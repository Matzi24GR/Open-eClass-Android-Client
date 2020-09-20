package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CalendarEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: DatabaseCalendarEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<DatabaseCalendarEvent>)

    @Query("SELECT * FROM calendar_event_table ORDER BY `end` DESC")
    fun getAllEvents(): LiveData<List<DatabaseCalendarEvent>>

    @Query("DELETE FROM  calendar_event_table")
    fun clear()

    @Query("SELECT * FROM calendar_event_table c WHERE NOT EXISTS ( SELECT * FROM calendar_sync_table s WHERE c.id = s.databaseId)")
    fun getEventsThatAreNotSynced(): List<DatabaseCalendarEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSyncedEvent(syncedEvent: DatabaseCalendarSyncId)

    @Query("SELECT calendarSyncId FROM calendar_sync_table")
    fun getAllSyncedEventsIds(): List<Long>

}