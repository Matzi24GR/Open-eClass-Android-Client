package com.geomat.openeclassclient.network

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