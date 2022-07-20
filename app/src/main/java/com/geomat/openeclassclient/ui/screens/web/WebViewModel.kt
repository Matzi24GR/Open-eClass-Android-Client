package com.geomat.openeclassclient.ui.screens.web

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.CredentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(val repository: CredentialsRepository) : ViewModel() {

    val credentials = repository.credentialsFlow

    fun refresh() {
        viewModelScope.launch {
            repository.checkTokenStatus()
        }
    }

}