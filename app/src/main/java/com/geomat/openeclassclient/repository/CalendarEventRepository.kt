package com.geomat.openeclassclient.repository

import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import com.geomat.openeclassclient.database.CalendarEvent
import com.geomat.openeclassclient.database.CalendarEventDao
import com.geomat.openeclassclient.network.CalendarResponse
import com.geomat.openeclassclient.network.eClassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CalendarEventRepository(private val calendarEventDao: CalendarEventDao) {

    val allEvents: LiveData<List<CalendarEvent>> = calendarEventDao.getAllEvents()

    suspend fun insertAll(events: List<CalendarEvent>) {
        calendarEventDao.insertAll(events)
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            calendarEventDao.clear()
        }
    }

    suspend fun refreshData(token: String) {

        withContext(Dispatchers.IO) {
            try {
                val result = eClassApi.JsonApi.getCalendar("PHPSESSID=$token").execute()
                if (result.isSuccessful) {
                    val responseList = result.body()?.result
                    val dbList = mutableListOf<CalendarEvent>()

                    for (i in responseList!!.indices) {
                        with(responseList[i]){
                            dbList.add(
                                CalendarEvent(
                                    this.id.toLong(),
                                    this.title,
                                    this.start.toLong(),
                                    this.end.toLong(),
                                    HtmlCompat.fromHtml(this.content, HtmlCompat.FROM_HTML_MODE_COMPACT).replace(Regex("""\(deadline: \d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\)"""),""),
                                    this.event_group,
                                    this.Class,
                                    this.event_type,
                                    this.course,
                                    this.url
                                )
                            )
                        }
                    }
                    insertAll(dbList)
                }
            } catch (cause: Throwable) {
            }
        }

    }

}