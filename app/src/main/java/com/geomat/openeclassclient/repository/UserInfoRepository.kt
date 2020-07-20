package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import com.geomat.openeclassclient.database.UserInfo
import com.geomat.openeclassclient.database.UserInfoDao
import com.geomat.openeclassclient.network.EclassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import timber.log.Timber

class UserInfoRepository(private val userDao: UserInfoDao) {

    fun getUserWithUsername(username: String) :LiveData<UserInfo> {
        return userDao.getUserWithUsername(username)
    }

    fun insertUser(user: UserInfo) {
        userDao.insert(user)
    }

    suspend fun refreshData(token: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = EclassApi.HtmlParser.getMainPage("PHPSESSID=$token").execute()
                if (response.isSuccessful) {
                    val document = Jsoup.parse(response.body())
                    val infoBox = document.select("div [id=profile_box]")
                    val username = infoBox.select("div [class=not_visible text-center]").text()
                    val fullName = infoBox.select("a")[0].text()
                    val category = infoBox.select("span[class=tag-value text-muted]")[0].text()
                    val imgUrl = infoBox.select("img").attr("src")

                    insertUser(
                        UserInfo(username,
                            fullName,
                            category,
                            imgUrl
                        )
                    )
                } else {
                    Timber.i("UserInfo Refresh Failed")
                }
            } catch (cause: Throwable) {
            }
        }

    }
}