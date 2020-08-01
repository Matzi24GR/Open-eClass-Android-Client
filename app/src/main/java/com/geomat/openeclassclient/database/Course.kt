package com.geomat.openeclassclient.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.geomat.openeclassclient.domain.Course

@Entity(tableName = "courses_table")
data class DatabaseCourse(

    @PrimaryKey
    val id: String,                  // ex. DAI107

    var title: String,               // ex.  Βάσεις Δεδομένων ΙΙ - ΠΛ0601
    var desc: String,                // ex.  ""
    var announcementFeedUrl: String = ""

)

fun List<DatabaseCourse>.asDomainModel(): List<Course> {
    return map {
        Course(
            id = it.id,
            title = it.title,
            desc = it.desc,
            announcementFeedUrl = it.announcementFeedUrl
        )
    }
}