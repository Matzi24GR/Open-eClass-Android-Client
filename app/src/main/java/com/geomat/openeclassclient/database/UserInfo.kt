package com.geomat.openeclassclient.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info_table")
data class UserInfo(

    @PrimaryKey
    var username: String,           // ex.  xyz2068
    var fullName: String,           // ex.  John Smith
    var category: String,           // ex.  Undergraduate » Comp Sci
    var imageUrl: String            // ex.  /template/default/img/default_256.png
)