package com.geomat.openeclassclient.ui

import androidx.lifecycle.ViewModel
import com.geomat.openeclassclient.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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