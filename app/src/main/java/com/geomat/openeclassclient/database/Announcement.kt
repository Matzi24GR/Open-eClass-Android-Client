package com.geomat.openeclassclient.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.geomat.openeclassclient.domain.Announcement

@Entity(tableName = "announcements_table",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCourse::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("courseId"),
            onDelete = ForeignKey.CASCADE)
    ]
)
data class DatabaseAnnouncement(

    @PrimaryKey
    var id: String,                  // ex.  15 or s15 for system announcements
    var courseId: String?,           // ex.  DAI104 or null for system announcements

    var title: String,               // ex.  ΠΑΡΑΤΑΣΗ ΗΛΕΚΤΡΟΝΙΚΗΣ ΑΞΙΟΛΟΓΗΣΗΣ ΔΙΔΑΚΤΙΚΟΥ ΕΡΓΟΥ ΕΑΡΙΝΟΥ ΕΞΑΜΗΝΟΥ 2019-2020
    var link: String,                // ex.  https://openeclass.uom.gr/modules/announcements/main_ann.php?aid=15
    var description: String,         // ex.  Σας ενημερώνουμε ότι η ηλεκτρονική αξιολόγηση διδακτικού έργου για το ....
    var date: Long                   // ex.  1591786714000

)

data class DatabaseAnnouncementWithCourseName(
    var id: String,                  // ex.  15 or s15 for system announcements
    var courseId: String?,           // ex.  DAI104 or null for system announcements
    var courseName: String?,         // ex.  Βάσεις Δεδομένων
    var title: String,               // ex.  ΠΑΡΑΤΑΣΗ ΗΛΕΚΤΡΟΝΙΚΗΣ ΑΞΙΟΛΟΓΗΣΗΣ ΔΙΔΑΚΤΙΚΟΥ ΕΡΓΟΥ ΕΑΡΙΝΟΥ ΕΞΑΜΗΝΟΥ 2019-2020
    var link: String,                // ex.  https://openeclass.uom.gr/modules/announcements/main_ann.php?aid=15
    var description: String,         // ex.  Σας ενημερώνουμε ότι η ηλεκτρονική αξιολόγηση διδακτικού έργου για το ....
    var date: Long                   // ex.  1591786714000

)

fun List<DatabaseAnnouncementWithCourseName>.asDomainModel(): List<Announcement> {
    return map {
        Announcement(
            id = it.id,
            courseId = it.courseId,
            courseName = it.courseName,
            title = it.title,
            link = it.link,
            description = it.description,
            date = it.date
        )
    }
}