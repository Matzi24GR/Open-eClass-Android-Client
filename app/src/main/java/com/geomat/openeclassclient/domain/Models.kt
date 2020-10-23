package com.geomat.openeclassclient.domain

import com.geomat.openeclassclient.database.DatabaseAnnouncement

data class Announcement(
    var id: String,                  // ex.  15 or s15 for system announcements
    var courseId: String?,           // ex.  DAI104 or null for system announcements
    var courseName: String?,         // ex.  Βάσεις Δεδομένων
    var title: String,               // ex.  ΠΑΡΑΤΑΣΗ ΗΛΕΚΤΡΟΝΙΚΗΣ ΑΞΙΟΛΟΓΗΣΗΣ ΔΙΔΑΚΤΙΚΟΥ ΕΡΓΟΥ ΕΑΡΙΝΟΥ ΕΞΑΜΗΝΟΥ 2019-2020
    var link: String,                // ex.  https://openeclass.uom.gr/modules/announcements/main_ann.php?aid=15
    var description: String,         // ex.  Σας ενημερώνουμε ότι η ηλεκτρονική αξιολόγηση διδακτικού έργου για το ....
    var date: Long,                  // ex.  1591786714000
    var isRead: Boolean

)

data class CalendarEvent(

    val id: Long,                   // ex. 18

    var title: String,              // ex.  "Βάσεις Δεδομένων: Εργασία 1"
    var start: Long = 0L,           // ex.  1586811540000
    var end: Long = 0L,             // ex.  1586811540000
    var content: String,            // ex.  "&lt;p&gt;Δείτε το συνημμένο.&lt;/p&gt;\n(deadline: 2020-04-13 23:59:00)"
    var event_group: String,        // ex.  "deadline"
    var Class: String,              // ex.  "event-important"
    var event_type: String,         // ex.  "assignment"
    var courseCode: String?,        // ex.  "DAI104" or null for user events
    var url: String                 // ex.  "https://openeclass.uom.gr/modules/work/index.php?id=18&course=DAI104"
)

data class Course(

    val id: String,                 // ex. DAI107

    var title: String,              // ex.  Βάσεις Δεδομένων ΙΙ - ΠΛ0601
    var desc: String                // ex.  ""
)

data class UserInfo(
    var username: String,           // ex.  xyz2068
    var fullName: String,           // ex.  John Smith
    var category: String,           // ex.  Undergraduate » Comp Sci
    var imageUrl: String            // ex.  /template/default/img/default_256.png
)