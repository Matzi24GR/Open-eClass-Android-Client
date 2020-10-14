package com.geomat.openeclassclient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.geomat.openeclassclient.database.UserInfoDao
import com.geomat.openeclassclient.database.asDomainModel
import com.geomat.openeclassclient.domain.UserInfo
import com.geomat.openeclassclient.network.DataTransferObjects.UserInfoResponse
import com.geomat.openeclassclient.network.DataTransferObjects.asDatabaseModel
import com.geomat.openeclassclient.network.EclassApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class UserInfoRepository @Inject constructor(private val userDao: UserInfoDao) {

    fun getUserWithUsername(username: String) :LiveData<UserInfo> {
        return Transformations.map(userDao.getUserWithUsername(username)) {
            if (it != null) {
                return@map it.asDomainModel()
            }
            return@map null
        }
    }

    suspend fun refreshData(token: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = EclassApi.HtmlParser.getMainPage("PHPSESSID=$token").await()
                userDao.insert(UserInfoResponse(response).asDatabaseModel())
            } catch (e: Exception) {
                Timber.i(e)
            }
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            userDao.clearAll()
        }
    }
}