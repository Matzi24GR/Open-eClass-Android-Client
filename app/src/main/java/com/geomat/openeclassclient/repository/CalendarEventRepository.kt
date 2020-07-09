package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import com.geomat.openeclassclient.database.CalendarEvent
import com.geomat.openeclassclient.database.CalendarEventDao

class CalendarEventRepository(private val calendarEventDao: CalendarEventDao) {

    val allEvents: LiveData<List<CalendarEvent>> = calendarEventDao.getAllEvents()

    suspend fun insertAll(events: List<CalendarEvent>) {
        calendarEventDao.insertAll(events)
    }

}