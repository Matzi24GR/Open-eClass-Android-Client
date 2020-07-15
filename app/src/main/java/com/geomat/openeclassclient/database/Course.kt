package com.geomat.openeclassclient.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses_table")
data class Course(

    @PrimaryKey
    val id: String,                 // ex. DAI107

    var title: String,              // ex.  Βάσεις Δεδομένων ΙΙ - ΠΛ0601
    var desc: String,                // ex.  ""
    var announcementFeedUrl: String
)