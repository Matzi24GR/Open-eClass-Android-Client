package com.geomat.openeclassclient.ui.screens.main

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.ui.components.BottomNav
import com.geomat.openeclassclient.ui.screens.NavGraphs
import com.geomat.openeclassclient.ui.screens.destinations.AnnouncementScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.CalendarScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.CourseListScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.HomeScreenDestination
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@RootNavGraph
@NavGraph
annotation class LoginNavGraph(
    val start: Boolean = false
)

@RootNavGraph(start = true)
@NavGraph
annotation class MainNavGraph(
    val start: Boolean = false,
    val default: Boolean = true
)


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun OpenEclassApp(isLoggedIn: Boolean) {
    val navController = rememberAnimatedNavController()
    val showBottomBar = remember {
        mutableStateOf(false)
    }

    val navHostEngine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopCenter,
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
    )

    Scaffold(
        bottomBar = { if (showBottomBar.value) BottomNav(navController = navController) }
    ) {
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            navController = navController,
            modifier = Modifier.padding(it),
            engine = navHostEngine
        )
        LaunchedEffect(Unit) {
            if (!isLoggedIn) {
                navController.navigate(NavGraphs.login) {
                    popUpTo(NavGraphs.root) {
                        PopUpToBuilder()
                    }
                }
            }
        }
    }

    val currentDestination: NavDestination? = navController.currentBackStackEntryAsState().value?.destination
    val bottomBarDestinations = remember {
        BottomBarDestination.values().map {
            it.direction.route
        }
    }
    if (currentDestination != null) {
        showBottomBar.value = bottomBarDestinations.contains(currentDestination.route)
    }
}

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(HomeScreenDestination(), Icons.Default.Home, R.string.home_tab),
    Announcements(AnnouncementScreenDestination(), Icons.Default.Announcement, R.string.announcements_tab),
    Courses(CourseListScreenDestination(), Icons.Default.ViewList, R.string.courses_tab),
    Calendar(CalendarScreenDestination(), Icons.Default.Event, R.string.calendar_tab)
}
