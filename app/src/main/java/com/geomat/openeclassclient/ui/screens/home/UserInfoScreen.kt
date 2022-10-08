package com.geomat.openeclassclient.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.domain.UserInfo
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.ui.components.OpenEclassTopBar
import com.geomat.openeclassclient.ui.components.TokenExpirationBanner
import com.geomat.openeclassclient.ui.screens.main.MainNavGraph
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@Destination
@MainNavGraph(start = true)
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
        Column(
            Modifier
                .animateContentSize()
                .padding(it)) {
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
    Surface(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp)), elevation = 8.dp) {
        val surfaceColor = MaterialTheme.colors.background
        val highlightColor = Color.Gray
        Column {
            Row {
                GlideImage(
                    imageModel = userInfo.value?.imageUrl,
                    component = rememberImageComponent {
                        CircularRevealPlugin()
                        +ShimmerPlugin(baseColor = surfaceColor, highlightColor = highlightColor)
                    },
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .padding(PaddingValues(0.dp, 0.dp, 8.dp, 8.dp))
                        .clip(RoundedCornerShape(bottomEnd = 16.dp)),
                    failure = { Image(painter = painterResource(id = R.drawable.ic_default_user), contentDescription = "") }
                )
                Column(Modifier.padding(start = 8.dp, top = 4.dp)) {
                    Text(text = userInfo.value?.fullName ?: "John Smith", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 4.dp).placeholder(visible = userInfo.value == null, surfaceColor, shape = RoundedCornerShape(4.dp), highlight = PlaceholderHighlight.shimmer(highlightColor)))
                    Text(text = userInfo.value?.username ?: "xyz2068", color = MaterialTheme.colors.primary, modifier = Modifier.placeholder(visible = userInfo.value == null, surfaceColor, shape = RoundedCornerShape(4.dp),  highlight = PlaceholderHighlight.shimmer(highlightColor)))
                }
            }
            Text(text = userInfo.value?.category?.replace(" » ","\n") ?: "Undergraduate » Comp Sci",
                Modifier
                    .padding(8.dp)
                    .placeholder(
                        visible = userInfo.value == null,
                        surfaceColor,
                        shape = RoundedCornerShape(4.dp),
                        highlight = PlaceholderHighlight.shimmer(highlightColor)
                    ))
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
                "Undergraduate Comp Sci",
                "/template/default/img/default_256.png"
            )
        )
    }
    Column(Modifier.padding(12.dp)) {
        UserInfoCard(userInfo)
    }
}