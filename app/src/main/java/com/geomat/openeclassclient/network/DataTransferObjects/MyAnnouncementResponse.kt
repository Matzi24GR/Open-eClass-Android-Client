package com.geomat.openeclassclient.network.DataTransferObjects

data class MyAnnouncementResponse (

    val iTotalDisplayRecords : Int,
    val iTotalRecords : Int,
    val aaData : List<List<String>>
)