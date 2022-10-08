package com.geomat.openeclassclient.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.ui.screens.destinations.ExternalAuthScreenDestination
import com.geomat.openeclassclient.ui.screens.login.serverSelect.AuthTypeParcel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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