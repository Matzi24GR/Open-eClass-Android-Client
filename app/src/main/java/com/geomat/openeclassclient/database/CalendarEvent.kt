package com.geomat.openeclassclient.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_event_table")
data class CalendarEvent(

    @PrimaryKey
    val id: Long,                   // ex. 18

    var title: String,              // ex.  "Βάσεις Δεδομένων: Εργασία 1"
    var start: Long = 0L,           // ex.  1586811540000
    var end: Long = 0L,             // ex.  1586811540000
    var content: String,            // ex.  "&lt;p&gt;Δείτε το συνημμένο.&lt;/p&gt;\n(deadline: 2020-04-13 23:59:00)"
    var event_group: String,        // ex.  "deadline"
    var Class: String,              // ex.  "event-important"
    var event_type: String,         // ex.  "assignment"
    var courseCode: String,         // ex.  "DAI104"
    var url: String                 // ex.  "https://openeclass.uom.gr/modules/work/index.php?id=18&course=DAI104"
)