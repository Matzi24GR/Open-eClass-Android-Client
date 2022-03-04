package com.geomat.openeclassclient.ui.Calendar

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import androidx.lifecycle.ViewModel
import com.geomat.openeclassclient.domain.CalendarEvent
import com.geomat.openeclassclient.repository.CalendarEventRepository
import dagger.hilt.android.scopes.ViewScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@ViewScoped
class CalendarViewModel @Inject constructor(private val repo: CalendarEventRepository): ViewModel() {

    data class Calendar(
        val id: Long,
        val displayName: String,
        val accountName: String,
        val ownerName: String
    )

    fun getCalendars(contentResolver: ContentResolver): List<Calendar> {
        val EVENT_PROJECTION: Array<String> = arrayOf(
            CalendarContract.Calendars._ID,                     // 0
            CalendarContract.Calendars.ACCOUNT_NAME,            // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,           // 3
            CalendarContract.Calendars.ACCOUNT_TYPE             // 4

        )

        // The indices for the projection array above.
        val PROJECTION_ID_INDEX: Int = 0
        val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
        val PROJECTION_DISPLAY_NAME_INDEX: Int = 2
        val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3
        val PROJECTION_ACCOUNT_TYPE_INDEX: Int = 4

        // Run query
        val uri: Uri = CalendarContract.Calendars.CONTENT_URI
        val cursor: Cursor? = contentResolver.query(uri, EVENT_PROJECTION, null, null, null)


        val calendarList = mutableListOf<Calendar>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Get the field values
                val calID: Long = cursor.getLong(PROJECTION_ID_INDEX)
                val displayName: String = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX)
                val accountName: String = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                val accountType: String = cursor.getString(PROJECTION_ACCOUNT_TYPE_INDEX)
                // Do something with the values...
                calendarList.add(Calendar(calID, displayName, accountName, accountType))
                Timber.i(
                    "ID:%s    Name:%s    AccountName:%s    AccountType:%s",
                    calID.toString(),
                    displayName,
                    accountName,
                    accountType
                )
            }
            if (cursor.count == 0) Timber.i("No Calendars Found")
            cursor.close()
        }
        return calendarList
    }

    fun insertEventToCalendar(
        event: CalendarEvent,
        selectedCal: Long,
        contentResolver: ContentResolver
    ) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, event.start)
            put(CalendarContract.Events.DTEND, event.end)
            put(CalendarContract.Events.TITLE, event.title)
            put(CalendarContract.Events.DESCRIPTION, event.content)
            put(CalendarContract.Events.CALENDAR_ID, selectedCal)
            put("eventTimezone", TimeZone.getDefault().id)
        }
        val EventUri: Uri = contentResolver.insert(
            CalendarContract.Events.CONTENT_URI,
            values
        )!!

        val eventID: Long? = EventUri.lastPathSegment?.toLong()
        if (eventID != null) {
            Timber.i("Inserted Event with calendar id: %d", eventID)
            GlobalScope.launch(Dispatchers.IO) {
                repo.insertSyncedEvent(eventID, event.id)
            }
        }
    }

    fun deleteAllEvents(contentResolver: ContentResolver) {
        GlobalScope.launch(Dispatchers.IO) {
            val syncedIds = repo.getSyncedIds()
            syncedIds.forEach {
                val deleteUri: Uri =
                    ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it)
                val rows: Int = contentResolver.delete(deleteUri, null, null)
                if (rows == 1) {
                    repo.removeSyncedEvent(it)
                }
            }
        }
    }
}
