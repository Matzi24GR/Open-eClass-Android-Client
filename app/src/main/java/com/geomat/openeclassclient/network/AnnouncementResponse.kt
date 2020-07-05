package com.geomat.openeclassclient.network

data class AnnouncementResponse (

    val iTotalDisplayRecords : Int,
    val iTotalRecords : Int,
    val aaData : List<List<String>>
)