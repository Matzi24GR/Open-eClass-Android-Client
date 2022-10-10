package com.geomat.openeclassclient.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.PopUpToBuilder
import com.geomat.openeclassclient.BuildConfig
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.repository.*
import com.geomat.openeclassclient.ui.screens.NavGraphs
import com.geomat.openeclassclient.ui.screens.destinations.AboutScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.DebugScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TopBarViewModel @Inject constructor(
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

@Composable
fun OpenEclassTopBar(
    title: String,
    navigator: DestinationsNavigator,
    navigateBack: Boolean = true,
    viewModel: TopBarViewModel = hiltViewModel(),
    showMoreButtons: Boolean = true
) {
    var showMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val actions: @Composable RowScope.() -> Unit = {
        if (showMoreButtons) {
            IconButton(onClick = {
                navigator.navigate(NavGraphs.login) {
                    popUpTo(NavGraphs.root) {
                        PopUpToBuilder()
                    }
                }
                scope.launch(Dispatchers.IO) {
                    viewModel.logout()
                }
            }) {
                Icon(Icons.Default.Logout, "")
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, "")
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(onClick = { navigator.navigate(AboutScreenDestination) }) {
                    Text(text = stringResource(id = R.string.about))
                }
                if (BuildConfig.DEBUG) {
                    DropdownMenuItem(onClick = { navigator.navigate(DebugScreenDestination) }) {
                        Text(text = "DEBUG")
                    }
                }
            }
        } else {
            IconButton(onClick = { navigator.navigate(AboutScreenDestination) }) {
                Icon(Icons.Default.Info, "")
            }
        }
    }

    if (navigateBack) {
        TopAppBar(
            title = { Text(text = title, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) },
            actions = actions,
            navigationIcon = {
                if (navigateBack)
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "")
                    }
            }
        )
    } else {
        TopAppBar(
            title = { Text(text = title) },
            actions = actions
        )
    }

}