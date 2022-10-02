package com.geomat.openeclassclient.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.geomat.openeclassclient.network.HostSelectionInterceptor
import com.geomat.openeclassclient.network.OpenEclassService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import timber.log.Timber
import java.io.IOException
import java.security.cert.CertPathValidatorException
import javax.inject.Inject

data class Credentials (
    var username: String = "",
    var password: String = "",
    var token: String = "",
    var serverUrl: String = "",
    var isLoggedIn: Boolean = false,
    var tokenExpired: Boolean = false,
    var selectedAuthName: String = "",
    var selectedAuthUrl: String = "",
    var usesExternalAuth: Boolean = selectedAuthUrl.isNotBlank()
)

class CredentialsRepository @Inject constructor(private val dataStore: DataStore<Preferences>, private val openEclassService: OpenEclassService, private val hostSelectionInterceptor: HostSelectionInterceptor) {

    suspend fun login(credentials: Credentials) {
       // withContext(Dispatchers.IO) {
            updateFullCredentials(credentials = credentials)
            setInterceptor()
            val response = openEclassService.getToken(credentials.username, credentials.password).awaitResponse()
            if (response.isSuccessful) {
                Timber.i("response successful")
                val token = response.body()
                when (token) {
                    "FAILED" -> throw Exception("FAILED")
                    "NOTENABLED" -> throw Exception("NOTENABLED")
                    else -> {
                        Timber.i("Login Response: ${response.body()}")
                        credentials.isLoggedIn = true
                        credentials.tokenExpired = false
                        if (token != null) {
                            credentials.token = token
                        } else {
                            Timber.e("Returned Token is Null")
                        }
                        updateFullCredentials(credentials = credentials)
                    }
                }
            } else {
                throw Exception("RESPONSE")
            }
      //  }
    }

    val credentialsFlow: Flow<Credentials> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences: Preferences ->
            val username = preferences[PreferencesKeys.USERNAME] ?: ""
            val password = preferences[PreferencesKeys.PASSWORD] ?: ""
            val token = preferences[PreferencesKeys.TOKEN] ?: ""
            val serverUrl = preferences[PreferencesKeys.SERVER_URL] ?: ""
            val isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
            val tokenExpired = preferences[PreferencesKeys.TOKEN_EXPIRED] ?: false
            val selectedAuthName = preferences[PreferencesKeys.SELECTED_AUTH_NAME] ?: ""
            val selectedAuthUrl = preferences[PreferencesKeys.SELECTED_AUTH_URL] ?: ""
            Credentials(username, password, token, serverUrl, isLoggedIn, tokenExpired, selectedAuthName, selectedAuthUrl)
        }

    suspend fun setInterceptor() {
        val url = credentialsFlow.first().serverUrl
        if (url.isNotBlank()) {
            Timber.i("Got Url: $url")
            hostSelectionInterceptor.setHost(url)
        }
    }

    suspend fun checkTokenStatus() {
        withContext(Dispatchers.IO) {
            try {
                credentialsFlow.collect {
                    val result = openEclassService.checkTokenStatus(it.token).awaitResponse()
                    Timber.i("TokenStatus: ${result.body()}")
                    if (result.body().toString().contains("EXPIRED")) {
                        if (it.usesExternalAuth) {
                            setTokenExpired()
                        } else {
                            login(it)
                        }
                    }
                }

            } catch (e: CertPathValidatorException) {
                Timber.e(e)
            } catch (e: Exception) {
                Timber.i(e)
            }
        }
    }

    suspend fun setTokenExpired() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN_EXPIRED] = true
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            credentialsFlow.collect {
                openEclassService.logout(it.token)
                updateFullCredentials(Credentials())
            }
            checkTokenStatus()
        }
    }

    suspend fun updateFullCredentials(credentials: Credentials) {
        Timber.i("Updating Full Credentials")
        dataStore.edit { preferences ->
            with(credentials) {
                preferences[PreferencesKeys.USERNAME] = username
                preferences[PreferencesKeys.PASSWORD] = password
                preferences[PreferencesKeys.TOKEN] = token
                preferences[PreferencesKeys.SERVER_URL] = serverUrl
                preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
                preferences[PreferencesKeys.TOKEN_EXPIRED] = tokenExpired
                preferences[PreferencesKeys.SELECTED_AUTH_NAME] = selectedAuthName
                preferences[PreferencesKeys.SELECTED_AUTH_URL] = selectedAuthUrl
            }
        }
    }

    private object PreferencesKeys {
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
        val TOKEN = stringPreferencesKey("token")
        val SERVER_URL = stringPreferencesKey("server_url")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val TOKEN_EXPIRED = booleanPreferencesKey("token_expired")
        val SELECTED_AUTH_NAME = stringPreferencesKey("selected_auth_name")
        val SELECTED_AUTH_URL = stringPreferencesKey("selected_auth_url")
    }

}