package com.geomat.openeclassclient.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.geomat.openeclassclient.domain.UserInfo

@Entity(tableName = "user_info_table")
data class DatabaseUserInfo(

    @PrimaryKey
    var username: String,           // ex.  xyz2068
    var fullName: String,           // ex.  John Smith
    var category: String,           // ex.  Undergraduate » Comp Sci
    var imageUrl: String            // ex.  /template/default/img/default_256.png
)

fun DatabaseUserInfo.asDomainModel(): UserInfo {
    return UserInfo(
        username = username,
        fullName = fullName,
        category = category,
        imageUrl = imageUrl
    )
}