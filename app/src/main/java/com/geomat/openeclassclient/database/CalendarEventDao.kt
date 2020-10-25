package com.geomat.openeclassclient.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CalendarEventDao {

    @Update
    fun updateAll(events: List<DatabaseCalendarEvent>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(events: List<DatabaseCalendarEvent>): List<Long>

    @Query("SELECT * FROM calendar_event_table ORDER BY `end` DESC")
    fun getAllEvents(): LiveData<List<DatabaseCalendarEvent>>

    @Query("DELETE FROM  calendar_event_table")
    fun clear()

    @Query("DELETE FROM calendar_event_table WHERE id NOT IN (:ids)")
    fun clearNotInList(ids: List<Long>)

    @Query("SELECT * FROM calendar_event_table c WHERE NOT EXISTS ( SELECT * FROM calendar_sync_table s WHERE c.id = s.databaseId)")
    fun getEventsThatAreNotSynced(): List<DatabaseCalendarEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSyncedEvent(syncedEvent: DatabaseCalendarSyncId)

    @Query("DELETE FROM calendar_sync_table WHERE calendarSyncId = :syncedId")
    fun removeSyncedEvent(syncedId: Long)

    @Query("SELECT calendarSyncId FROM calendar_sync_table")
    fun getAllSyncedEventsIds(): List<Long>

}