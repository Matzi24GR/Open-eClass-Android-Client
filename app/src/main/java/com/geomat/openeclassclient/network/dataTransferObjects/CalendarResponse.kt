package com.geomat.openeclassclient.network.dataTransferObjects

import androidx.core.text.HtmlCompat
import com.geomat.openeclassclient.database.DatabaseCalendarEvent
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CalendarResponse(
    @Json(name = "success") val success: Int,
    @Json(name = "result")  val result:  List<Result>,
    @Json(name = "cid")     val cid:     String?
)

@JsonClass(generateAdapter = true)
data class Result(
    @Json (name = "id")          val id:          String,
    @Json (name = "title")       val title:       String,
    @Json (name = "start")       val start:       String,
    @Json (name = "startdate")   val startdate:   String,
    @Json (name = "duration")    val duration:    String,
    @Json (name = "end")         val end:         String,
    @Json (name = "content")     val content:     String,
    @Json (name = "event_group") val event_group: String,
    @Json (name = "class")       val Class:       String,
    @Json (name = "event_type")  val event_type:  String,
    @Json (name = "course")      val course:      String,
    @Json (name = "start_hour")  val start_hour:  String,
    @Json (name = "end_hour")    val end_hour:    String,
    @Json (name = "url")         val url:         String,
)

fun CalendarResponse.asDatabaseModel(): List<DatabaseCalendarEvent> {
    return result.map {
        DatabaseCalendarEvent(
            id = it.id.toLong(),
            title = HtmlCompat.fromHtml(it.title,HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
            start = it.start.toLong(),
            end = it.end.toLong(),
            content = HtmlCompat.fromHtml(it.content,HtmlCompat.FROM_HTML_MODE_LEGACY)
                .replace(
                    Regex("""\(deadline: \d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\)"""), ""),
            event_group = it.event_group,
            Class = it.Class,
            event_type = it.event_type,
            courseCode = it.course.ifBlank { null },
            url = it.url
        )
    }
}