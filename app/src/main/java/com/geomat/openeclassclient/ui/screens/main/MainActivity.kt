package com.geomat.openeclassclient.ui.screens.main

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.geomat.openeclassclient.BuildConfig
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.repository.CredentialsRepository
import com.geomat.openeclassclient.ui.screens.NavGraphs
import com.geomat.openeclassclient.ui.screens.destinations.*
import com.geomat.openeclassclient.ui.screens.login.serverSelect.AuthTypeParcel
import com.geomat.openeclassclient.ui.theme.OpenEclassClientTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
lateinit var clipboardManager: ClipboardManager

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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var credentialsRepository: CredentialsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            credentialsRepository.setInterceptor()
            credentialsRepository.credentialsFlow.collect {
                runOnUiThread {
                    setContent { OpenEclassClientTheme { OpenEclassApp(it.isLoggedIn) } }
                }
            }
            credentialsRepository.checkTokenStatus()
        }
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    }

    private fun deleteFiles() {
        // Cached Files in /temp folder
        val tempDir = File(filesDir, "/temp")
        var files = tempDir.listFiles()
        if (files != null) {
            for (file: File in files) {
                if (file.lastModified() < Instant.now().toEpochMilli() - TimeUnit.MINUTES.toMillis(60) )
                    file.delete()
            }
        }
        // Files download by older versions, TODO: Remove after a while
        files = filesDir.listFiles()
        if (files != null) {
            for (file: File in files) {
                if (file.isFile) {
                    file.delete()
                }
            }
        }
    }

    override fun onStart() {
        deleteFiles()
        super.onStart()
    }

    override fun onDestroy() {
        deleteFiles()
        super.onDestroy()
    }

}

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
                navController.navigate(NavGraphs.login)
            }
        }
    }

    val currentDestination: NavDestination? =
        navController.currentBackStackEntryAsState().value?.destination
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
    viewModel: MainViewModel? = hiltViewModel(),
    showMoreButtons: Boolean = true
) {
    var showMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val actions: @Composable RowScope.() -> Unit = {
        if (showMoreButtons) {
            IconButton(onClick = {
                navigator.navigate(NavGraphs.login) {
                    popUpTo(NavGraphs.login.route)
                }
                scope.launch(Dispatchers.IO) {
                    viewModel?.logout()
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
    val currentDestination: NavDestination? =
        navController.currentBackStackEntryAsState().value?.destination

    BottomNavigation {
        BottomBarDestination.values().forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                        launchSingleTop = true
                    })
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

