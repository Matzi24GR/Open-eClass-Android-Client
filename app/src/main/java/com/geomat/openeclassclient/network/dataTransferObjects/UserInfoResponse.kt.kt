package com.geomat.openeclassclient.network.dataTransferObjects

import com.geomat.openeclassclient.database.DatabaseUserInfo
import org.jsoup.Jsoup
import timber.log.Timber

class UserInfoResponse (page: String) {
    private val document = Jsoup.parse(page)

    var username: String = ""
    var fullName: String = ""
    var category: String = ""
    var imgUrl: String = ""


    init {
        try {
            val infoBox = document.select("div [id=profile_box]")

            val usernameElement= infoBox.select("div [class=not_visible text-center]")
            val fullNameElement = infoBox.select("a").first()
            val categoryElement = infoBox.select("span[class=tag-value text-muted]").first()
            val imgUrlElement = infoBox.select("img")

            username = usernameElement.text()
            if (fullNameElement != null) {
                fullName = fullNameElement.text()
            }
            if (categoryElement != null) {
                category = categoryElement.text()
            }
            imgUrl = imgUrlElement.attr("src")
        } catch (e: Exception) {
            Timber.i(e)
        }
    }

}

fun UserInfoResponse.asDatabaseModel(): DatabaseUserInfo {
    return DatabaseUserInfo(
        username = username,
        fullName = fullName,
        imageUrl = imgUrl,
        category = category
    )
}