package com.geomat.openeclassclient.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.geomat.openeclassclient.domain.CalendarEvent

@Entity(tableName = "calendar_event_table",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCourse::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("courseCode"),
            onDelete = ForeignKey.CASCADE)
    ]
)
data class DatabaseCalendarEvent(

    @PrimaryKey
    val id: Long,                   // ex. 18
    @ColumnInfo(index = true)
    var courseCode: String?,        // ex.  "DAI104" or null for user events

    var title: String,              // ex.  "Βάσεις Δεδομένων: Εργασία 1"
    var start: Long = 0L,           // ex.  1586811540000
    var end: Long = 0L,             // ex.  1586811540000
    var content: String,            // ex.  "&lt;p&gt;Δείτε το συνημμένο.&lt;/p&gt;\n(deadline: 2020-04-13 23:59:00)"
    var event_group: String,        // ex.  "deadline"
    var Class: String,              // ex.  "event-important"
    var event_type: String,         // ex.  "assignment"
    var url: String                 // ex.  "https://openeclass.uom.gr/modules/work/index.php?id=18&course=DAI104"
)

@Entity(tableName = "Calendar_sync_table",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCalendarEvent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("databaseId"),
            onDelete = ForeignKey.SET_NULL)
    ]
)
data class DatabaseCalendarSyncId(

    @PrimaryKey
    var calendarSyncId: Long,
    @ColumnInfo(index = true)
    var databaseId: Long

)

fun List<DatabaseCalendarEvent>.asDomainModel(): List<CalendarEvent> {
    return map {
        CalendarEvent(
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
}