package com.geomat.openeclassclient.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.CoursesRepository
import com.geomat.openeclassclient.repository.Credentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CourseListViewModel @Inject constructor(
    val repo: CoursesRepository,
    private val credentials: Flow<Credentials>
) : ViewModel() {

    val courses = repo.allCourses

    fun refresh() {
        viewModelScope.launch {
            try {
                credentials.collect {
                    repo.refreshData(it.token)
                }
            } catch (e: AssertionError) {
                Timber.i(e)
            }
        }
    }

}