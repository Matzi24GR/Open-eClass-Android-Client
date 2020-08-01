package com.geomat.openeclassclient.network.DataTransferObjects

import com.geomat.openeclassclient.database.DatabaseUserInfo
import org.jsoup.Jsoup

class UserInfoResponse (page: String) {
    private val document = Jsoup.parse(page)
    private val infoBox = document.select("div [id=profile_box]")

    val username = infoBox.select("div [class=not_visible text-center]").text()
    val fullName = infoBox.select("a")[0].text()
    val category = infoBox.select("span[class=tag-value text-muted]")[0].text()
    val imgUrl = infoBox.select("img").attr("src")
}

fun UserInfoResponse.asDatabaseModel(): DatabaseUserInfo {
    return DatabaseUserInfo(
        username = username,
        fullName = fullName,
        imageUrl = imgUrl,
        category = category
    )
}