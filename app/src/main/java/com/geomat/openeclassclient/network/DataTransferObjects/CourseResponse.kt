package com.geomat.openeclassclient.network.DataTransferObjects

import com.geomat.openeclassclient.database.DatabaseCourse
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "courses")
class CourseResponse {

    @Element(name = "coursegroup")
    lateinit var courseGroup: CourseGroup

}

@Xml(name = "coursegroup")
class CourseGroup {

    @Attribute(name="name")
    var name: String = ""

    @Element(name="course")
    lateinit var courseList: List<NetworkCourse>

}
@Xml(name = "course")
class NetworkCourse {
    @Attribute(name = "code")
    var code: String = ""
    @Attribute(name = "title")
    var title: String = ""
    @Attribute(name = "description")
    var description: String = ""

}

fun CourseResponse.asDatabaseModel(): List<DatabaseCourse> {
    return courseGroup.courseList.map {
        DatabaseCourse(
            id = it.code,
            title = it.title,
            desc = it.description
        )
    }
}

