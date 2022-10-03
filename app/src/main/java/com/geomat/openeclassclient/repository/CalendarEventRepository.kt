package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.CalendarEventDao
import com.geomat.openeclassclient.database.DatabaseCalendarEvent
import com.geomat.openeclassclient.database.DatabaseCalendarSyncId
import com.geomat.openeclassclient.database.asDomainModel
import com.geomat.openeclassclient.domain.CalendarEvent
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.OpenEclassService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class CalendarEventRepository @Inject constructor(private val calendarEventDao: CalendarEventDao, private val openEclassService: OpenEclassService) {

    val allEvents: LiveData<List<CalendarEvent>> = Transformations.map(calendarEventDao.getAllEvents()){
        it.asDomainModel()
    }

    val nextEvent: LiveData<CalendarEvent?> = Transformations.map(calendarEventDao.getNextEvent(System.currentTimeMillis()*1000)) {
        if (it != null) {
            return@map CalendarEvent(
                id = it.id,
                title = it.title,
                start = it.start,
                end = it.end,
                content = it.content,
                event_group = it.event_group,
                Class = it.Class,
                event_type = it.event_type,
                courseCode = it.courseCode,
                url = it.url
            )
        }
        return@map null
    }

    suspend fun getSyncedIds(): List<Long> {
        return withContext(Dispatchers.IO) {
            calendarEventDao.getAllSyncedEventsIds()
        }
    }

    suspend fun getNotSyncedEvents(): List<CalendarEvent> {
        return withContext(Dispatchers.IO) {
            calendarEventDao.getEventsThatAreNotSynced().asDomainModel()
        }
    }

    suspend fun insertSyncedEvent(calendarId: Long, databaseId:Long){
        withContext(Dispatchers.IO) {
            calendarEventDao.insertSyncedEvent(DatabaseCalendarSyncId(calendarId, databaseId))
        }
    }

    suspend fun removeSyncedEvent(calendarId: Long){
        withContext(Dispatchers.IO) {
            calendarEventDao.removeSyncedEvent(calendarId)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            calendarEventDao.clear()
        }
    }

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            try {
                //Get Events
                val calendar = openEclassService.getCalendar().await()
                val events = calendar.asDatabaseModel()
                //Insert Events
                val result = calendarEventDao.insertAll(events)
                //Update events that failed to insert
                val toUpdate = mutableListOf<DatabaseCalendarEvent>()
                for (i in result.indices) {
                    if (result[i] == -1L) {
                        toUpdate.add(events[i])
                    }
                }
                calendarEventDao.updateAll(toUpdate)
                //Remove Deleted Events
                val toRetain = events.map {it.id}
                calendarEventDao.clearNotInList(toRetain)
            } catch (e: Exception) {
                Timber.i(e)
            }
        }

    }

}