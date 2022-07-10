package com.geomat.openeclassclient.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.domain.Tool

@Entity(tableName = "courses_table")
data class DatabaseCourse(

    @PrimaryKey
    val id: String,                  // ex. DAI107

    var title: String,               // ex.  Βάσεις Δεδομένων ΙΙ - ΠΛ0601
    var desc: String,                // ex.  ""

    var imageUrl: String,
    var tools: String

)

fun DatabaseCourse.asDomainModel(): Course {
    val toolStrings = tools.split(";").toList()
    return Course(
        id = id,
        title = title,
        desc = desc,
        imageUrl = imageUrl,
        tools = toolStrings.map { Tool(isHandled = false, name = it) }
    )
}

fun List<DatabaseCourse>.asDomainModel(): List<Course> {
    return map { it.asDomainModel() }
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

    @ColumnInfo(index = true)
    var courseId: String

)