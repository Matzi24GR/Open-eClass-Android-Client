package com.geomat.openeclassclient.ui.screens.web

import androidx.lifecycle.ViewModel
import com.geomat.openeclassclient.repository.CredentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(val repository: CredentialsRepository) : ViewModel() {

    val credentials = repository.credentialsFlow

}