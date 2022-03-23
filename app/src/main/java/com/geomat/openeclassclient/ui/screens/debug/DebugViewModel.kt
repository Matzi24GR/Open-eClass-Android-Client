package com.geomat.openeclassclient.ui.screens.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.CredentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(val repository: CredentialsRepository) : ViewModel() {

    val credentials = repository.credentialsFlow

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.checkTokenStatus()
        }
    }

}