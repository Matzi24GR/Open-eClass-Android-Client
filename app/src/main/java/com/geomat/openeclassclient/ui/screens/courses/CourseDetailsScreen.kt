package com.geomat.openeclassclient.ui.screens.courses

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.domain.Tool
import com.geomat.openeclassclient.ui.components.HtmlText
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage

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
    ) {
        viewModel.refresh(course)
        CourseDetailsScreenContent(viewModel.uiState)
    }

}

@Composable
private fun CourseDetailsScreenContent(uiState: MutableState<CourseDetailsState>) {
    if (uiState.value.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
    uiState.value.course.tools.let {
        LazyColumn (modifier = Modifier
            .padding(8.dp)
            .animateContentSize()) {
            item {
                infoCard(uiState)
            }
            items(it) {
                if (it.name.isNotBlank()) {
                    ToolRow(name = it.name)
                }
            }
        }
    }
}

@Composable
private fun infoCard(uiState: MutableState<CourseDetailsState>) {
    var expanded by remember { mutableStateOf(false) }
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
                GlideImage(
                    imageModel = uiState.value.course.imageUrl,
                    contentScale = ContentScale.Crop,
                    circularReveal = CircularReveal(),
                )
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

@Composable
private fun ToolRow(name: String, onClick: () -> Unit = {}) {
    val textId = remember {
        when (name) {
            "docs" -> R.string.tool_docs
            "announcements" -> R.string.announcements_tab
            "exercise" -> R.string.tool_exercise
            "gradebook" -> R.string.tool_gradebook
            "glossary" -> R.string.tool_glossary
            "mindmap" -> R.string.tool_mindmap
            "assignments" -> R.string.tool_assignments
            "questionnaire" -> R.string.tool_questionnaire
            "calendar" -> R.string.calendar_tab
            "blog" -> R.string.tool_blog
            "dropbox" -> R.string.tool_dropbox
            "groups" -> R.string.tool_groups
            "attendance" -> R.string.tool_attendance
            "videos" -> R.string.tool_videos
            "fa-trophy" -> R.string.tool_fa_trophy
            "forum" -> R.string.tool_forum
            "links" -> R.string.tool_links
            "fa-list" -> R.string.tool_fa_list
            else -> null
        }
    }
    Surface(
        Modifier
            .padding(4.dp, 2.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (textId != null) {
                Text(stringResource(id = textId), fontWeight = FontWeight.Bold, modifier = Modifier.width(400.dp))
            }
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
            Tool(false, "assignements"),
            Tool(false, "questionaire"),
            Tool(false, "conference"),
            Tool(false, "forum"),
        )
    )
    )))
}
