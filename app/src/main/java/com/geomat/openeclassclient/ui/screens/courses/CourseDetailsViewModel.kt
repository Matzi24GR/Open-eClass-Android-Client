package com.geomat.openeclassclient.ui.screens.courses

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.repository.CoursesRepository
import com.geomat.openeclassclient.repository.Credentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    val repo: CoursesRepository,
    private val credentials: Flow<Credentials>
) : ViewModel() {

    var uiState: MutableState<CourseDetailsState> = mutableStateOf(CourseDetailsState())
        private set

    fun refresh(course: Course) {
        uiState.value = CourseDetailsState(true, course)
        viewModelScope.launch {
            try {
                credentials.collect { it ->
                    repo.updateCourseDetails(it.token, course)
                    repo.getCourseFlow(course).collect { course ->
                        uiState.value = CourseDetailsState(false, course)
                    }
                }
            } catch (e: AssertionError) {
                Timber.i(e)
            }
        }
    }

}

data class CourseDetailsState(
    val loading: Boolean = true,
    val course: Course = Course("","","","", emptyList())
)
