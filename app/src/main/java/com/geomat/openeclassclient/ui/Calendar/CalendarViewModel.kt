package com.geomat.openeclassclient.ui.Calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.geomat.openeclassclient.database.EClassDatabase
import com.geomat.openeclassclient.domain.CalendarEvent
import com.geomat.openeclassclient.repository.CalendarEventRepository

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalendarEventRepository

    val allEvents: LiveData<List<CalendarEvent>>

    init {
        val calendarEventDao = EClassDatabase.getInstance(application).calendarEventDao
        repository = CalendarEventRepository(calendarEventDao)
        allEvents = repository.allEvents
    }

}