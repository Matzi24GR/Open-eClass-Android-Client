package com.geomat.openeclassclient.ui.screens.calendar

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.domain.CalendarEvent
import com.geomat.openeclassclient.ui.components.HtmlText
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat

@Destination
@Composable
fun CalendarScreen(navigator: DestinationsNavigator, viewModel: CalendarViewModel = hiltViewModel()) {
    viewModel.refresh()
    Scaffold(topBar = {
        OpenEclassTopBar(
            title = stringResource(id = R.string.calendar_tab),
            navigator = navigator,
            navigateBack = false
        )
    }) {
        ScreenContent(data = viewModel.calendarEvents.observeAsState(), Modifier.padding(it))
    }
}

@Composable
private fun ScreenContent(data: State<List<CalendarEvent>?>, modifier: Modifier) {
    val listState = rememberLazyListState()
    val nextEvent = remember { mutableStateOf(-1) }
    val scope = rememberCoroutineScope()

    // Event List
    data.value?.let {
        LazyColumn(modifier.animateContentSize(), state = listState) {
            itemsIndexed(it) { index, item ->
                if (index == nextEvent.value) {
                    Row(Modifier.fillMaxWidth()) {
                        Divider(
                            color = Color.Red,
                            thickness = 4.dp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                CalendarItem(item = item)
            }
        }
    }
    // Auto scroll to current
    data.value?.let {
        nextEvent.value = getNextEventIndex(it)
    }
    nextEvent.value.let {
        if (it != -1) {
            LaunchedEffect(it) {
                scope.launch {
                    listState.scrollToItem(it, -100)
                }
            }
        }
    }

}

fun getNextEventIndex(list: List<CalendarEvent>): Int {
    val time = System.currentTimeMillis()
    list.forEachIndexed { index, event ->
        if (event.start >= time) {
            return index
        }
    }
    return list.size
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun CalendarItem(item: CalendarEvent) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        // Time Card
        Card(
            modifier = Modifier
                .padding(end = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .padding(2.dp)
            ) {
                val yearFormat = SimpleDateFormat("y")
                val monthFormat = SimpleDateFormat("MMM")
                val dayNameFormat = SimpleDateFormat("E")
                val dayFormat = SimpleDateFormat("d")
                Text(text = dayNameFormat.format(item.start))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = dayFormat.format(item.start),
                            style = TextStyle(fontSize = 22.sp)
                        )
                    }
                }
                Text(text = monthFormat.format(item.start))
                if (yearFormat.format(item.start) != yearFormat.format(System.currentTimeMillis())) {
                    Text(text = yearFormat.format(item.start))
                }
                Divider(Modifier.width(40.dp), thickness = 2.dp)
                Text(text = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(item.start))
                if (item.start != item.end) {
                    Divider(Modifier.width(20.dp))
                    Text(text = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(item.end))
                }
            }
        }
        // Content Card
        Card {
            Column(Modifier.padding(8.dp).fillMaxWidth()) {
                Text(text = item.title)
                HtmlText(html = item.content, darkThemeEnabled = isSystemInDarkTheme())
//                Text(text = item.Class)
//                Text(text = item.event_group)
//                Text(text = item.event_type)
//                Text(text = item.url)
//                Text(text = item.courseCode.toString())
//                Text(text = item.id.toString())

            }
        }

    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    val list = listOf(
        CalendarEvent(
            15,
            "Title",
            content = "content",
            event_group = "group",
            Class = "class",
            event_type = "type",
            url = "url",
            courseCode = "code",
            end = 2L
        )
    )
    val data = remember {
        mutableStateOf(list)
    }
    ScreenContent(data = data, Modifier)
}