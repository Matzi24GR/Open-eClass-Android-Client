package com.geomat.openeclassclient.network.DataTransferObjects

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyAnnouncementResponse (

    @Json(name = "iTotalDisplayRecords")
    val iTotalDisplayRecords : Int,

    @Json(name = "iTotalRecords")
    val iTotalRecords : Int,

    @Json(name = "aaData")
    val aaData : List<List<String>>
)