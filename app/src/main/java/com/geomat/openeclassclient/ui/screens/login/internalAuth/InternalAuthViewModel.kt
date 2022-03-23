package com.geomat.openeclassclient.ui.screens.login.internalAuth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.repository.CredentialsRepository
import com.geomat.openeclassclient.ui.screens.login.serverSelect.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InternalAuthViewModel @Inject constructor(private val repo: CredentialsRepository) :
    ViewModel() {

    // Selected Server
    private val _selectedServer = MutableLiveData(
        Server("", "")
    )
    val selectedServer: LiveData<Server>
        get() = _selectedServer

    val success = mutableStateOf(false)
    val wrongCredentials = mutableStateOf(false)
    val connectionError = mutableStateOf(false)
    val notEnabled = mutableStateOf(false)

    fun updateSelectedServer(server: Server) {
        _selectedServer.value = server
    }

    fun login(username: String, password: String) {
        val credentials = Credentials(
            username = username,
            password = password,
            serverUrl = selectedServer.value!!.url
        )
        viewModelScope.launch {
            try {
                repo.login(credentials)
                success.value = true
            } catch (e: Exception) {
                when (e.message) {
                    "FAILED" -> wrongCredentials.value = true
                    "NOTENABLED" -> notEnabled.value = true
                    "RESPONSE" -> connectionError.value = true
                }
            }

        }
    }
}