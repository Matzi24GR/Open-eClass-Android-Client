package com.geomat.openeclassclient.ui.screens.debug

import android.content.ClipData
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.network.EclassApi
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.ui.components.DropDownCard
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.geomat.openeclassclient.ui.screens.main.clipboardManager
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException

@Destination
@Composable
fun DebugScreen(navigator: DestinationsNavigator, viewModel: DebugViewModel = hiltViewModel()) {
    val credentials = viewModel.credentials.collectAsState(initial = Credentials())
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { OpenEclassTopBar(title = "DEBUG", navigator = navigator) }) {
        Column {
            DropDownCard(text = "Credentials") {
                Button(onClick = {
                    viewModel.logout()
                }, Modifier.fillMaxWidth()) {
                    Text(text = "Logout (Delete Credentials)")
                }
                Button(onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                EclassApi.MobileApi.logout(credentials.value.token).await()
                            } catch (e: CertPathValidatorException) {
                                Timber.i(e)
                            }
                            viewModel.refresh()
                        }
                    }
                }, Modifier.fillMaxWidth()) {
                    Text(text = "Logout (Invalidate Token)")
                }
                Button(onClick = {
                    viewModel.refresh()
                }, Modifier.fillMaxWidth()) {
                    Text(text = "Check Token")
                }
                CredentialList(credentials = credentials.value)
            }
        }

    }

}

@Composable
private fun CredentialList(credentials: Credentials) {
    Column {
        DebugText(name = "Username", text = credentials.username)
        DebugText(name = "Password", text = credentials.password)
        DebugText(name = "Token", text = credentials.token)
        DebugText(name = "ServerURL", text = credentials.serverUrl)
        DebugText(name = "AuthURL", text = credentials.selectedAuthUrl)
        DebugText(name = "IsLoggedIn", text = credentials.isLoggedIn.toString())
        DebugText(name = "TokenExpired", text = credentials.tokenExpired.toString())
        DebugText(name = "UsesExternalAuth", text = credentials.usesExternalAuth.toString())
    }
}

@Composable
private fun DebugText(name: String, text: String) {
    Row(
        Modifier
            .padding(12.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name)
        Text(text = text)
        IconButton(onClick = {
            val clip: ClipData = ClipData.newPlainText("debug", text)
            clipboardManager.setPrimaryClip(clip)
        }) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "")
        }
    }
}