package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.CalendarEventDao
import com.geomat.openeclassclient.database.DatabaseCalendarSyncId
import com.geomat.openeclassclient.database.asDomainModel
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

    val syncedIDs = calendarEventDao.getAllSyncedEventsIds()
    val notSyncedEvents = calendarEventDao.getEventsThatAreNotSynced()

    suspend fun insertSyncedEvent(calendarId: Long, databaseId:Long){
        withContext(Dispatchers.IO) {
            calendarEventDao.insertSyncedEvent(DatabaseCalendarSyncId(calendarId, databaseId))
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
                val calendar = EclassApi.JsonApi.getCalendar("PHPSESSID=$token").await()
                calendarEventDao.insertAll(calendar.asDatabaseModel())
            } catch (e: Exception) {
                Timber.i(e)
            }
        }

    }

}