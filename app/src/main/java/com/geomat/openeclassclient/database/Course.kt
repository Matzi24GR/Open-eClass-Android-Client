package com.geomat.openeclassclient.database

import androidx.annotation.Nullable
import androidx.room.*
import com.geomat.openeclassclient.domain.Course


@Entity(tableName = "courses_table")
data class DatabaseCourse(

    @PrimaryKey
    val id: String,                  // ex. DAI107

    var title: String,               // ex.  Βάσεις Δεδομένων ΙΙ - ΠΛ0601
    var desc: String                 // ex.  ""
)

fun List<DatabaseCourse>.asDomainModel(): List<Course> {
    return map {
        Course(
            id = it.id,
            title = it.title,
            desc = it.desc
        )
    }
}

@Entity(tableName = "feed_urls_table",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCourse::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("courseId"),
            onDelete = ForeignKey.CASCADE)
    ]
)
data class DatabaseFeedUrl(

    @PrimaryKey
    var announcementFeedUrl: String,

    var courseId: String
)