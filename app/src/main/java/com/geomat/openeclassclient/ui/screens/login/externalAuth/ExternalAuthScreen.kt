package com.geomat.openeclassclient.ui.screens.login.externalAuth

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.ui.components.CustomWebView
import com.geomat.openeclassclient.ui.components.OpenEclassTopBar
import com.geomat.openeclassclient.ui.screens.login.serverSelect.AuthTypeParcel
import com.geomat.openeclassclient.ui.screens.main.LoginNavGraph
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination
@LoginNavGraph
@Composable
fun ExternalAuthScreen(
    authType: AuthTypeParcel,
    viewModel: ExternalAuthViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    Scaffold(topBar = { OpenEclassTopBar(title = authType.name, navigator = navigator, showMoreButtons = false) }) {
        viewModel.authType = authType
        CustomWebView(authType.url, navigator = navigator, modifier = Modifier.padding(it))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    ExternalAuthScreen(
        authType = AuthTypeParcel("Name", "demo.openeclass.com"),
        navigator = EmptyDestinationsNavigator
    )
}