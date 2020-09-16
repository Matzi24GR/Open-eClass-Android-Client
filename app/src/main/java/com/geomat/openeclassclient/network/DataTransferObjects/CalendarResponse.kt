package com.geomat.openeclassclient.network.DataTransferObjects

import androidx.core.text.HtmlCompat
import com.geomat.openeclassclient.database.DatabaseCalendarEvent
import com.squareup.moshi.Json


data class CalendarResponse(
    val success: Int,
    val result: List<Result>,
    val cid: String?
)

data class Result(
    val id: String,
    val title: String,
    val start: String,
    val startdate: String,
    val duration: String,
    val end: String,
    val content: String,
    val event_group: String,
    @Json(name = "class") val Class: String,
    val event_type: String,
    val course: String,
    val start_hour: String,
    val end_hour : String,
    val url: String
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
            courseCode = if (it.course.isNotBlank()) it.course else null,
            url = it.url
        )
    }
}