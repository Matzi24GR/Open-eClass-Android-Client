package com.geomat.openeclassclient.network.dataTransferObjects

import com.geomat.openeclassclient.database.DatabaseCourse
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@kotlinx.serialization.Serializable
@XmlSerialName("courses","","")
data class CourseResponse (
    val courseGroup: CourseGroup
)

@kotlinx.serialization.Serializable
@XmlSerialName("coursegroup","","")
data class CourseGroup (
    val name: String,
    val courseList: List<NetworkCourse>
)

@kotlinx.serialization.Serializable
@XmlSerialName("course","","")
data class NetworkCourse (
    val code: String,
    val title: String,
    val description: String

)

fun CourseResponse.asDatabaseModel(): List<DatabaseCourse> {
    return courseGroup.courseList.map {
        DatabaseCourse(
            id = it.code,
            title = it.title,
            desc = it.description,
            imageUrl = "",
            tools = ""
        )
    }
}

