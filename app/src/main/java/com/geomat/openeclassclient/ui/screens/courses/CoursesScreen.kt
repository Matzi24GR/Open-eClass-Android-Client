package com.geomat.openeclassclient.ui.screens.courses

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.ui.screens.destinations.BareWebViewScreenDestination
import com.geomat.openeclassclient.ui.screens.destinations.CourseDetailsScreenDestination
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    Scaffold(
        topBar = {
            OpenEclassTopBar(
                title = stringResource(id = R.string.courses_tab),
                navigator = navigator,
                navigateBack = false
            )
        },
        floatingActionButton = {
            val title = stringResource(R.string.EditCourses)
            FloatingActionButton(onClick = {
                navigator.navigate(
                    BareWebViewScreenDestination(
                        title,
                        "/auth/courses.php"
                    )
                )
            }) {
                Icon(Icons.Filled.Edit, "edit courses")
            }
    }
    ) {
        val data = viewModel.courses.observeAsState()
        data.value?.let {
            LazyColumn {
                items(it) { course -> CourseRow(course = course) {
                    navigator.navigate(CourseDetailsScreenDestination(course))
                } }
            }
            if (it.isEmpty()) {
                Text(text = stringResource(id = R.string.no_results_found))
            }
        }
        viewModel.refresh()
    }

}

@Composable
private fun CourseRow(course: Course, onClick: () -> Unit = {}) {
    Surface(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }

    ) {
        val expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.animateContentSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(course.title, fontWeight = FontWeight.Bold, modifier = Modifier.width(400.dp))
//                IconButton(
//                    enabled = true,
//                    onClick = {expanded = !expanded},
//                    content = { if (expanded) Icon(Icons.Filled.KeyboardArrowUp,"") else Icon(Icons.Filled.KeyboardArrowDown,"")},
//                )
            }
            if (expanded) {
                Button(
                    onClick = { /*TODO*/ },
                    Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text(text = "Delete Course")
                }
            }
        }
    }

}

@Preview
@Composable
private fun Preview() {
    val data = remember {
        mutableStateOf(List(20) {
            Course(
                "DAI107",
                "Βάσεις Δεδομένων ΙΙ - ΠΛ0601",
                "",
                "",
                emptyList()
            )
        })
    }
    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text(text = stringResource(R.string.EditCourses)) },
            onClick = { /*TODO*/ },
            icon = {
                Icon(Icons.Filled.Edit, "add")
            })
    }) {
        data.value.let {
            LazyColumn {
                items(it) { course -> CourseRow(course = course) }
            }
            if (it.isEmpty()) {
                Text(text = "No Courses Found")
            }
        }
    }
}
