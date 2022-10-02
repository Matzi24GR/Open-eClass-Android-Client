package com.geomat.openeclassclient.ui.screens.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.network.OpenEclassService
import com.geomat.openeclassclient.repository.CredentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(val repository: CredentialsRepository, private val openEclassService: OpenEclassService) : ViewModel() {

    val credentials = repository.credentialsFlow

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun logoutNetworkCall() {
        viewModelScope.launch {
            openEclassService.logout(credentials.first().token)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.checkTokenStatus()
        }
    }

    fun getNewToken() {
        viewModelScope.launch {
            repository.login(credentials.first())
        }
    }

}