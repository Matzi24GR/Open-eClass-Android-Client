package com.geomat.openeclassclient.ui.screens.announcements

import android.text.format.DateUtils
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.domain.Announcement
import com.geomat.openeclassclient.ui.components.HtmlText
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun AnnouncementScreen(
    viewModel: AnnouncementScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val currentAnnouncement = remember {
        mutableStateOf(Announcement("", "", "", "", "", "", 0, false))
    }

    Scaffold(topBar = {
        OpenEclassTopBar(
            title = stringResource(id = R.string.announcements_tab),
            navigator = navigator,
            navigateBack = false
        )
    }) { paddingValues ->
        ModalBottomSheetLayout(
            sheetContent = { BottomSheet(announcement = currentAnnouncement) },
            sheetState = modalBottomSheetState,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Setup Data
            val data = viewModel.announcements.observeAsState()
            viewModel.refresh()
            // Announcement List
            data.value?.let {
                LazyColumn(Modifier.animateContentSize()) {
                    items(it) { announcement ->
                        AnnouncementRow(announcement = announcement, modifier = Modifier
                            .padding(8.dp)
                            .animateContentSize()
                        ) {
                            //onClick: setRead and open announcement
                            viewModel.setRead(announcement)
                            currentAnnouncement.value = announcement
                            scope.launch {
                                modalBottomSheetState.show()
                            }
                        }
                    }
                }
                // No results
                if (it.isEmpty()) {
                    Text(text = stringResource(id = R.string.no_results_found))
//                    val surfaceColor = MaterialTheme.colors.surface
//                    val highlightColor = MaterialTheme.colors.onSurface
//                    Column(modifier = Modifier.padding(paddingValues).padding(8.dp)) {
//                        for (i in 1..20) {
//                            Box(
//                                modifier = Modifier.clip(RoundedCornerShape(16.dp)).padding(8.dp).fillMaxWidth().height(80.dp).placeholder(
//                                    visible = true,
//                                    highlight = PlaceholderHighlight.shimmer(highlightColor),
//                                    color = surfaceColor
//                                )
//                            )
//                        }
//                    }
                }
            }
        }
    }
    BackHandler(enabled = modalBottomSheetState.currentValue == ModalBottomSheetValue.Expanded || modalBottomSheetState.currentValue == ModalBottomSheetValue.HalfExpanded) {
        scope.launch {
            modalBottomSheetState.hide()
        }
    }
}

@Composable
private fun AnnouncementRow(announcement: Announcement, modifier: Modifier, onClick: ()-> Unit) {
    Surface(modifier = modifier.clip(RoundedCornerShape(16.dp)).clickable {onClick() }, elevation = 8.dp) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .animateContentSize()
        ) {
            // Top Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = announcement.title, fontWeight = FontWeight.Bold, color = if (announcement.isRead) Color.Gray else MaterialTheme.colors.onSurface, modifier = Modifier
                        .fillMaxWidth(0.9F)
                        .weight(1F)
                )
                AnimatedVisibility(visible = !announcement.isRead) {
                    Surface(
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.newTag),
                            Modifier
                                .padding(2.dp)
                                .padding(horizontal = 4.dp),
                            color = MaterialTheme.colors.onSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
            // Main Content
            Surface(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxWidth()
            ) {
                HtmlText(
                    html = announcement.description,
                    modifier = Modifier.padding(8.dp),
                    isSystemInDarkTheme(),
                    maxLines = 5
                )
            }
            // Bottom Row
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier) {
                if (announcement.courseName.isNullOrBlank()) {
                    Text(
                        text = stringResource(id = R.string.Administrators),
                        Modifier.weight(1F),
                        fontWeight = FontWeight.SemiBold,
                        color = if (announcement.isRead) Color.Gray else MaterialTheme.colors.onSurface
                    )
                } else {
                    Text(
                        text = announcement.courseName!!,
                        Modifier.weight(1F),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        color = if (announcement.isRead) Color.Gray else MaterialTheme.colors.onSurface
                    )
                }
                Text(
                    text = DateUtils.getRelativeTimeSpanString(announcement.date).toString(),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier,
                    color = if (announcement.isRead) Color.Gray else MaterialTheme.colors.secondary,
                    fontWeight = if (announcement.isRead) FontWeight.Normal else FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun BottomSheet(announcement: MutableState<Announcement>) {
    Column(
        Modifier
            .verticalScroll(ScrollState(0))
            .fillMaxHeight()
            .padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Divider(
            Modifier
                .width(52.dp)
                .padding(bottom = 24.dp), thickness = 4.dp)
        Text(text = announcement.value.title, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(bottom = 8.dp))
        Surface(
            color = MaterialTheme.colors.background,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp)
        ) {
            HtmlText(
                html = announcement.value.description,
                darkThemeEnabled = isSystemInDarkTheme(),
                enableLinks = true,
                modifier = Modifier.padding(8.dp),
                darkTextColor = Color.White
            )
        }
        Text(text = SimpleDateFormat.getDateTimeInstance().format(announcement.value.date))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    val data = remember {
        mutableStateOf(List(20) {
            Announcement(
                "15",
                "DAI104",
                "Βάσεις Δεδομένων",
                "ΠΑΡΑΤΑΣΗ ΗΛΕΚΤΡΟΝΙΚΗΣ ΑΞΙΟΛΟΓΗΣΗΣ ΔΙΔΑΚΤΙΚΟΥ ΕΡΓΟΥ ΕΑΡΙΝΟΥ ΕΞΑΜΗΝΟΥ 2019-2020",
                "https://openeclass.uom.gr/modules/announcements/main_ann.php?aid=15",
                "Σας ενημερώνουμε ότι η ηλεκτρονική αξιολόγηση διδακτικού έργου για το ....",
                1591786714000,
                false
            )
        })
    }
    data.value.let {
        LazyColumn {
        }
        if (it.isEmpty()) {
            Text(text = "No Announcements Found")
        }
    }
}