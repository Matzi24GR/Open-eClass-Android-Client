package com.geomat.openeclassclient.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.repository.CoursesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CourseListViewModel @Inject constructor(
    val repo: CoursesRepository
) : ViewModel() {

    val courses = repo.allCourses

    fun refresh() {
        viewModelScope.launch {
            try {
                repo.refreshData()
            } catch (e: AssertionError) {
                Timber.i(e)
            }
        }
    }

}