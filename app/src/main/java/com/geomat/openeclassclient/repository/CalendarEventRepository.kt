package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.*
import com.geomat.openeclassclient.domain.CalendarEvent
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.EclassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CalendarEventRepository @Inject constructor(private val calendarEventDao: CalendarEventDao) {

    val allEvents: LiveData<List<CalendarEvent>> = Transformations.map(calendarEventDao.getAllEvents()){
        it.asDomainModel()
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

    suspend fun refreshData(token: String) {
        withContext(Dispatchers.IO) {
            try {
                val tokenStatus = EclassApi.MobileApi.checkTokenStatus(token).await()
                if (tokenStatus != "EXPIRED") {
                    //Get Events
                    val calendar = EclassApi.JsonApi.getCalendar("PHPSESSID=$token").await()
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
                }
            } catch (e: Exception) {
                Timber.i(e)
            }
        }

    }

}