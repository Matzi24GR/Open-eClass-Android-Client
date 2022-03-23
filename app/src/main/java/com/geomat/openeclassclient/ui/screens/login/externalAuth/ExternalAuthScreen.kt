package com.geomat.openeclassclient.ui.screens.login.externalAuth

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.ui.components.CustomWebView
import com.geomat.openeclassclient.ui.screens.login.serverSelect.AuthTypeParcel
import com.geomat.openeclassclient.ui.screens.main.LOGIN_NAV_GRAPH
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination(navGraph = LOGIN_NAV_GRAPH)
@Composable
fun ExternalAuthScreen(
    authType: AuthTypeParcel,
    viewModel: ExternalAuthViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    Scaffold(topBar = { OpenEclassTopBar(title = authType.name, navigator = navigator) }) {
        viewModel.authType = authType
        CustomWebView(authType.url, navigator = navigator)
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