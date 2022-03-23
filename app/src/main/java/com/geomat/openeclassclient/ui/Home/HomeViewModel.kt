package com.geomat.openeclassclient.ui.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.repository.UserInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val repo: UserInfoRepository, private val credentials: Flow<Credentials>) : ViewModel() {

    lateinit var username: String

    init {
        viewModelScope.launch { username = credentials.first().username }
    }

    val userInfo = repo.getUserWithUsername(username = username)

    val credentialFlow = credentials

    fun refresh() {
        viewModelScope.launch {
            credentials.collect {
                viewModelScope.launch { repo.refreshData(it.token) }
            }
        }
    }

}