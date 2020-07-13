package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import com.geomat.openeclassclient.database.Course
import com.geomat.openeclassclient.database.UserInfo
import com.geomat.openeclassclient.database.UserInfoDao
import com.geomat.openeclassclient.network.eClassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class UserInfoRepository(private val userDao: UserInfoDao) {

    fun getUserWithUsername(username: String) :LiveData<UserInfo> {
        return userDao.getUserWithUsername(username)
    }

    suspend fun insertUser(user: UserInfo) {
        userDao.insert(user)
    }

    suspend fun refreshData(token: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = eClassApi.HtmlParser.getMainPage("PHPSESSID=$token").execute()
                if (response.isSuccessful) {
                    val document = Jsoup.parse(response.body())
                    val infoBox = document.select("div [id=profile_box]")
Timber.i(infoBox.toString())
                    val username = infoBox.select("div [class=not_visible text-center]").text()
                    val fullName = infoBox.select("a").text()
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