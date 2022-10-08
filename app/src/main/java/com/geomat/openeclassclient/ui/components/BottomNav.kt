package com.geomat.openeclassclient.ui.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.geomat.openeclassclient.repository.AnnouncementRepository
import com.geomat.openeclassclient.ui.screens.NavGraphs
import com.geomat.openeclassclient.ui.screens.appCurrentDestinationAsState
import com.geomat.openeclassclient.ui.screens.destinations.Destination
import com.geomat.openeclassclient.ui.screens.main.BottomBarDestination
import com.geomat.openeclassclient.ui.screens.startAppDestination
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomNavViewModel @Inject constructor(announcementRepository: AnnouncementRepository): ViewModel() {
    val unreadAnnouncementCount = announcementRepository.unreadCount
    init {
        viewModelScope.launch { announcementRepository.refreshData() }
    }
}

@Composable
fun BottomNav(
    navController: NavController,
    viewModel: BottomNavViewModel = hiltViewModel()
) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    BottomNavigation {
        BottomBarDestination.values().forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                        launchSingleTop = true
                    })
                },
                icon = {
                    BadgedBox( badge = {
                        if (destination == BottomBarDestination.Announcements) {
                            val unreadAnnouncementCount by viewModel.unreadAnnouncementCount.observeAsState(0)
                            val badgeVisibility by remember(unreadAnnouncementCount) { mutableStateOf(unreadAnnouncementCount>0) }
                            if (badgeVisibility) {
                                Badge(backgroundColor = MaterialTheme.colors.secondary){ Text(unreadAnnouncementCount.toString()) }
                            }
                        }
                    }) {
                        Icon(destination.icon, stringResource(id = destination.label))
                    }
                },
                label = { Text(stringResource(id = destination.label), maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}