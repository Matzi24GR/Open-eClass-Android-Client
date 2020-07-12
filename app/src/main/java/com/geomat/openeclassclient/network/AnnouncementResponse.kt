package com.geomat.openeclassclient.network

import com.squareup.moshi.Json

data class AnnouncementResponse (

    val iTotalDisplayRecords : Int,
    val iTotalRecords : Int,
    val aaData : List<List<String>>
)