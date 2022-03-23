package com.geomat.openeclassclient.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.domain.UserInfo
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.geomat.openeclassclient.ui.screens.main.TokenExpirationBanner
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun HomeScreen(navigator: DestinationsNavigator, viewModel: HomeViewModel = hiltViewModel()) {
    Scaffold(topBar = {
        OpenEclassTopBar(
            title = stringResource(id = R.string.home_tab),
            navigator = navigator,
            navigateBack = false
        )
    }) {
        val userInfo = viewModel.userInfo.observeAsState()
        val credentials = viewModel.credentialFlow.collectAsState(initial = Credentials())
        Column(Modifier.animateContentSize()) {
            if (credentials.value.tokenExpired) TokenExpirationBanner(navigator, credentials.value)
            Column(
                Modifier
                    .padding(12.dp)
                    .animateContentSize(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserInfoCard(userInfo)
            }
        }

        viewModel.refresh()
    }

}

@Composable
private fun UserInfoCard(userInfo: State<UserInfo?>) {
    Card(elevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        Column {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_default_user),
                    contentDescription = "",
                    Modifier.size(80.dp)
                )
                Column {
                    Text(text = userInfo.value?.fullName ?: "error")
                    Text(text = userInfo.value?.username ?: "error")
                }
            }
            Text(text = userInfo.value?.category ?: "error")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    val userInfo = remember {
        mutableStateOf(
            UserInfo(
                "xyz2068",
                "John Smith",
                "Undergraduate » Comp Sci",
                "/template/default/img/default_256.png"
            )
        )
    }
    Column(Modifier.padding(12.dp)) {
        UserInfoCard(userInfo)
    }
}