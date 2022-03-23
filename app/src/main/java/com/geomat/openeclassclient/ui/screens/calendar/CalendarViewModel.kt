package com.geomat.openeclassclient.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.CalendarEventRepository
import com.geomat.openeclassclient.repository.CredentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarEventRepository: CalendarEventRepository,
    private val credentialsRepository: CredentialsRepository
) : ViewModel() {

    val calendarEvents = calendarEventRepository.allEvents

    fun refresh() {
        viewModelScope.launch {
            credentialsRepository.credentialsFlow.collect {
                calendarEventRepository.refreshData(it.token)
            }
        }

    }

}