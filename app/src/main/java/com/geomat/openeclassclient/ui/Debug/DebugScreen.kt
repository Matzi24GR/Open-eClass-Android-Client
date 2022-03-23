package com.geomat.openeclassclient.ui

import android.content.ClipData
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.ui.Debug.DebugViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun DebugScreen(navigator: DestinationsNavigator, viewModel: DebugViewModel = hiltViewModel()) {
    val credentials = viewModel.credentials.collectAsState(initial = Credentials())

    Scaffold(topBar = { OpenEclassTopBar(title = "DEBUG", navigator = navigator)}) {
        Column {
            Button(onClick = {
                viewModel.logout()
            }) {
                Text(text = "Logout")
            }
            CredentialList(credentials = credentials.value)
        }

    }

}

@Composable
private fun CredentialList(credentials: Credentials) {
    Column() {
        DebugText(name="Username", text = credentials.username)
        DebugText(name="Password", text = credentials.password)
        DebugText(name="Token", text = credentials.token)
        DebugText(name="ServerURL", text = credentials.serverUrl)
        DebugText(name="AuthURL", text = credentials.selectedAuthUrl)
        DebugText(name="IsLoggedIn", text = credentials.isLoggedIn.toString())
        DebugText(name="TokenExpired", text = credentials.tokenExpired.toString())
        DebugText(name="UsesExternalAuth", text = credentials.usesExternalAuth.toString())
    }
}

@Composable
private fun DebugText(name:String, text: String) {
    Row(
        Modifier
            .padding(12.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = name)
        Text(text = text)
        IconButton(onClick = {
            val clip: ClipData = ClipData.newPlainText("debug",text)
            clipboardManager.setPrimaryClip(clip)
        }) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "")
        }
    }
}