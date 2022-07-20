package com.geomat.openeclassclient.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.ui.components.HtmlText
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AboutScreen(navigator: DestinationsNavigator) {

    Scaffold(topBar = {
        OpenEclassTopBar(
            title = stringResource(id = R.string.about),
            navigator = navigator
        )
    }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Links")
            Text(text = "Github")
            HtmlText(html = "<a href='https://github.com/Matzi24GR/Open-eClass-Android-Client'>https://github.com/Matzi24GR/Open-eClass-Android-Client </a> ", darkThemeEnabled = isSystemInDarkTheme(), enableLinks = true)
            Text(text = "Play Store")
            HtmlText(html = "<a href='https://play.google.com/store/apps/details?id=com.geomat.openeclassclient'>https://play.google.com/store/apps/details?id=com.geomat.openeclassclient</a>", darkThemeEnabled = isSystemInDarkTheme(), enableLinks = true)
        }
    }
}