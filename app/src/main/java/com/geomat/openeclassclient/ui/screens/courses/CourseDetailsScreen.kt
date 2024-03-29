package com.geomat.openeclassclient.ui.screens.courses

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.domain.Tool
import com.geomat.openeclassclient.domain.Tools
import com.geomat.openeclassclient.ui.components.HtmlText
import com.geomat.openeclassclient.ui.components.OpenEclassTopBar
import com.geomat.openeclassclient.ui.screens.destinations.DocumentScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.WebViewScreenDestination
import com.geomat.openeclassclient.ui.theme.FontAwesome
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@Destination
@Composable
fun CourseDetailsScreen(
    viewModel: CourseDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    course: Course
) {
    Scaffold(
        topBar = {
            OpenEclassTopBar(
                title = course.title,
                navigator = navigator,
                navigateBack = true
            )
        }
    ) { paddingValues ->
        viewModel.refresh(course)
        CourseDetailsScreenContent(uiState = viewModel.uiState, modifier = Modifier.padding(paddingValues)) {
            if (it == Tools.DOCUMENTS.value) {
                navigator.navigate(DocumentScreenDestination(course))
            } else {
                navigator.navigate(WebViewScreenDestination(it, course))
            }
        }
    }
}

@Composable
private fun CourseDetailsScreenContent(uiState: MutableState<CourseDetailsState>, modifier: Modifier = Modifier, onClick: (tool: String) -> Unit = {}) {
    if (uiState.value.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
    uiState.value.course.tools.let {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp), modifier = modifier
                .padding(8.dp)
                .animateContentSize()
        ) {
            item(span = { GridItemSpan(this.maxLineSpan) }) {
                InfoCard(uiState)
            }
            items(it) {
                if (it.name.isNotBlank()) {
                    ToolRow(name = it.name) {
                        onClick(it.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(uiState: MutableState<CourseDetailsState>) {
    var expanded by remember { mutableStateOf(false) }
    if (uiState.value.course.desc.isNotEmpty() || uiState.value.course.desc.isNotEmpty()) {
        Surface(
            Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { }
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                if (uiState.value.course.imageUrl.isNotBlank()) {
                    val surfaceColor = MaterialTheme.colors.surface
                    val highlightColor = MaterialTheme.colors.onSurface
                    GlideImage(
                        imageModel = uiState.value.course.imageUrl,
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        component = rememberImageComponent {
                            CircularRevealPlugin()
                            +ShimmerPlugin(baseColor = surfaceColor, highlightColor = highlightColor)
                        }
                    )

                    if (uiState.value.course.desc.isNotBlank()) {
                        HtmlText(html = uiState.value.course.desc,
                            Modifier
                                .padding(4.dp)
                                .heightIn(0.dp, if (expanded) Int.MAX_VALUE.dp else 80.dp), isSystemInDarkTheme(), enableLinks = true)
                        IconButton(
                            enabled = true,
                            onClick = {expanded = !expanded},
                            content = { if (expanded) Icon(Icons.Filled.KeyboardArrowUp,"") else Icon(Icons.Filled.KeyboardArrowDown,"")},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }
            }
        }
    }

}

@Composable
private fun ToolRow(name: String, onClick: () -> Unit = {}) {
    val tool: Tools? = Tools.from(name)
    Surface(
        Modifier
            .padding(4.dp, 2.dp)
            .clip(RoundedCornerShape(4.dp))
            .width(180.dp)
            .defaultMinSize(minHeight = 64.dp)
            .clickable { onClick() }

    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = tool?.icon?.padEnd(3,' ') ?: "", fontFamily = FontAwesome, fontSize = 22.sp, color = MaterialTheme.colors.primary)
            Text(if (tool==null) name else stringResource(tool.stringResource), fontWeight = FontWeight.Bold)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showSystemUi = true)
@Composable
fun CourseDetailScreenPreview() {
    CourseDetailsScreenContent(uiState = mutableStateOf(CourseDetailsState(loading = false, course = Course(
        id = "DAI210",
        title = "test",
        desc = "blah blah",
        imageUrl = "",
        tools = listOf(
                Tool(false, "docs"),
                Tool(false, "announcements"),
                Tool(false, "assignments"),
                Tool(false, "questionnaire"),
                Tool(false, "conference"),
                Tool(false, "forum"),
            )
        )
    )))
}
