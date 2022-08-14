package com.geomat.openeclassclient.ui.screens.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.domain.Announcement
import com.geomat.openeclassclient.repository.AnnouncementRepository
import com.geomat.openeclassclient.repository.CoursesRepository
import com.geomat.openeclassclient.repository.CredentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AnnouncementScreenViewModel @Inject constructor(
    private val repo: AnnouncementRepository,
    private val coursesRepository: CoursesRepository,
    credentialsRepository: CredentialsRepository
) : ViewModel() {

    val announcements = repo.allAnnouncements
    val credentials = credentialsRepository.credentialsFlow

    fun refresh() {
        viewModelScope.launch {
            try {
                coursesRepository.refreshData(credentials.first().token)
                repo.refreshData()
            } catch (e: AssertionError) {
                Timber.e(e)
            }
        }
    }

    fun setRead(announcement: Announcement) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.setRead(announcement)
            }
        }
    }

}