package com.geomat.openeclassclient.ui.screens.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.domain.Announcement
import com.geomat.openeclassclient.repository.AnnouncementRepository
import com.geomat.openeclassclient.repository.CredentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AnnouncementScreenViewModel @Inject constructor(
    private val repo: AnnouncementRepository,
    credentialsRepository: CredentialsRepository
) : ViewModel() {

    val announcements = repo.allAnnouncements
    val credentials = credentialsRepository.credentialsFlow

    fun refresh() {
        viewModelScope.launch {
            repo.refreshData()
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