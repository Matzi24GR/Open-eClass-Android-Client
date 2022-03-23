package com.geomat.openeclassclient.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.geomat.openeclassclient.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AboutScreen(navigator: DestinationsNavigator) {

    Scaffold(topBar = { OpenEclassTopBar(title = stringResource(id = R.string.about), navigator = navigator)}) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Links")
            Text(text = "Github")
            Text(text = "https://github.com/Matzi24GR/Open-eClass-Android-Client")
            Text(text = "Play Store")
            Text(text = "https://play.google.com/store/apps/details?id=com.geomat.openeclassclient")
        }
    }
}