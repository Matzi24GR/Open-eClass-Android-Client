package com.geomat.openeclassclient.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val coursesRepository: CoursesRepository,
    private val userInfoRepository: UserInfoRepository,
    private val calendarEventRepository: CalendarEventRepository,
    private val credentialsRepository: CredentialsRepository
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            credentialsRepository.credentialsFlow.collect {
                credentialsRepository.checkTokenStatus()
            }
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            announcementRepository.clear()
            coursesRepository.clear()
            userInfoRepository.clear()
            calendarEventRepository.clear()
            credentialsRepository.logout()
        }
    }

}