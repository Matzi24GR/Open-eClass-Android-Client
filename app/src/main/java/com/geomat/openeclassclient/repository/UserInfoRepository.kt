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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber
import javax.inject.Inject

class UserInfoRepository @Inject constructor(private val userDao: UserInfoDao, private val credentialsRepository: CredentialsRepository) {

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

                val host = credentialsRepository.credentialsFlow.first().serverUrl

                //Get UserInfo
                val response = EclassApi.HtmlParser.getMainPage("PHPSESSID=$token").await()
                val userInfo = UserInfoResponse(response).asDatabaseModel()
                userInfo.imageUrl = "https://" + host + userInfo.imageUrl
                //Insert UserInfo
                val result = userDao.insert(userInfo)
                //Update UserInfo if failed to Insert
                if (result == -1L) { userDao.update(userInfo) }
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