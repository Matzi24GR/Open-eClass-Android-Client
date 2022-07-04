package com.geomat.openeclassclient.ui.screens.main

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.geomat.openeclassclient.BuildConfig
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.repository.CredentialsRepository
import com.geomat.openeclassclient.ui.screens.NavGraphs
import com.geomat.openeclassclient.ui.screens.destinations.*
import com.geomat.openeclassclient.ui.screens.login.serverSelect.AuthTypeParcel
import com.geomat.openeclassclient.ui.screens.navDestination
import com.geomat.openeclassclient.ui.theme.OpenEclassClientTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.navigateTo
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

const val LOGIN_NAV_GRAPH = "login"
lateinit var clipboardManager: ClipboardManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var credentialsRepository: CredentialsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OpenEclassClientTheme { OpenEclassApp(credentialsRepository) } }

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            credentialsRepository.setInterceptor()
            credentialsRepository.checkTokenStatus()
        }
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    }

}

@Composable
fun OpenEclassApp(repository: CredentialsRepository) {
    val credentials = repository.credentialsFlow.collectAsState(initial = Credentials())

    val navController = rememberNavController()
    val showBottomBar = remember {
        mutableStateOf(false)
    }
    Scaffold(
        bottomBar = { if (showBottomBar.value) BottomNav(navController = navController) }
    ) {
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            navController = navController,
            modifier = Modifier.padding(it)
        )
        LaunchedEffect(Unit) {
            if (!credentials.value.isLoggedIn) {
                navController.navigateTo(NavGraphs.login)
            }
        }
    }
    val currentDestination: Destination? =
        navController.currentBackStackEntryAsState().value?.navDestination
    val bottomBarDestinations = remember {
        BottomBarDestination.values().map {
            it.direction.route
        }
    }
    if (currentDestination != null) {
        showBottomBar.value = bottomBarDestinations.contains(currentDestination.route)
    }
}

@Composable
fun OpenEclassTopBar(
    title: String,
    navigator: DestinationsNavigator,
    navigateBack: Boolean = true,
    viewModel: MainViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val actions: @Composable RowScope.() -> Unit = {
        IconButton(onClick = {
            scope.launch {
                viewModel.logout()
            }
            navigator.navigate(NavGraphs.login)
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
    }

    if (navigateBack) {
        TopAppBar(
            title = { Text(text = title) },
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

@Composable
fun TokenExpirationBanner(navigator: DestinationsNavigator, credentials: Credentials) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), Arrangement.SpaceBetween) {
            Text(text = "Token Expired, Please Sign in Again")
            OutlinedButton(modifier = Modifier, onClick = {
                navigator.navigate(
                    ExternalAuthScreenDestination(
                        AuthTypeParcel(
                            name = credentials.selectedAuthName,
                            url = credentials.selectedAuthUrl
                        )
                    )
                )
            }) {
                Text(text = "Login")
            }
        }
    }

}

@Composable
fun BottomNav(
    navController: NavController
) {
    val currentDestination: Destination? =
        navController.currentBackStackEntryAsState().value?.navDestination

    BottomNavigation {
        BottomBarDestination.values().forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigateTo(destination.direction) {
                        launchSingleTop = true
                    }
                },
                icon = { Icon(destination.icon, stringResource(id = destination.label)) },
                label = { Text(stringResource(id = destination.label,), maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(HomeScreenDestination(), Icons.Default.Home, R.string.home_tab),
    Announcements(
        AnnouncementScreenDestination(),
        Icons.Default.Announcement,
        R.string.announcements_tab
    ),
    Courses(CourseListScreenDestination(), Icons.Default.ViewList, R.string.courses_tab),
    Calendar(CalendarScreenDestination(), Icons.Default.Event, R.string.calendar_tab)
}

