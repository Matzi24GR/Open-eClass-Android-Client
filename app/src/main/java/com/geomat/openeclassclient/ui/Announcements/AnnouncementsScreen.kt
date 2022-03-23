package com.geomat.openeclassclient.ui.Announcements

import android.graphics.Color
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.domain.Announcement
import com.geomat.openeclassclient.ui.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun AnnouncementScreen(viewModel: AnnouncementScreenViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val currentAnnouncement = remember {
        mutableStateOf(Announcement("","","","","","",0,false))
    }

    Scaffold(topBar = { OpenEclassTopBar(title = stringResource(id = R.string.announcements_tab), navigator = navigator, navigateBack = false) }) {

        ModalBottomSheetLayout(sheetContent = { BottomSheet(announcement = currentAnnouncement)}, sheetState = modalBottomSheetState) {
            val data = viewModel.announcements.observeAsState()
            data.value?.let {
                LazyColumn(Modifier.animateContentSize() ) {
                    items(it) {announcement -> AnnouncementRow(announcement = announcement, modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            viewModel.setRead(announcement)
                            currentAnnouncement.value = announcement
                            scope.launch {
                                modalBottomSheetState.show()
                            }
                        }
                        .animateContentSize()
                    )}
                }
                if (it.isEmpty()) {
                    Text(text = stringResource(id = R.string.no_results_found))
                }
            }
            viewModel.refresh()
        }
    }
}

@Composable
private fun AnnouncementRow(announcement: Announcement, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)) {
                Text(text = announcement.title, fontWeight = FontWeight.Bold, modifier = Modifier
                    .fillMaxWidth(0.9F)
                    .weight(1F))
                if (!announcement.isRead) {
                    Surface(color = MaterialTheme.colors.secondary, shape = RoundedCornerShape(8.dp,8.dp,0.dp,8.dp)) {
                        Text(text = stringResource(R.string.newTag),
                            Modifier
                                .padding(2.dp)
                                .padding(horizontal = 4.dp), color = MaterialTheme.colors.onSecondary, maxLines = 1)
                    }
                }
            }
            Surface(color = MaterialTheme.colors.background, shape = RoundedCornerShape(12.dp), modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()) {
                HtmlText(html = announcement.description, modifier = Modifier.padding(8.dp), isSystemInDarkTheme(), maxLines = 5)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier) {
                if (announcement.courseName.isNullOrBlank()) {
                    Text(text = stringResource(id = R.string.Administrators),Modifier.weight(1F), fontWeight = FontWeight.SemiBold)
                } else {
                    Text(text = announcement.courseName!!,Modifier.weight(1F), maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                }
                Text(text = DateUtils.getRelativeTimeSpanString(announcement.date).toString(), textAlign = TextAlign.End, maxLines = 1, modifier = Modifier)
            }
        }
    }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier, darkThemeEnabled: Boolean, maxLines: Int = Integer.MAX_VALUE, enableLinks:Boolean = false) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context).apply {
            if (darkThemeEnabled) {
                setTextColor(Color.WHITE)
            }
            if (enableLinks) {
                linksClickable = true
                movementMethod = LinkMovementMethod.getInstance()
            }
            setMaxLines(maxLines)
            ellipsize = TextUtils.TruncateAt.MIDDLE
        } },
        update = {
            it.text = html.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT).trim() }
    )
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    val data = remember { mutableStateOf( List(20) {
        Announcement(
            "15",
            "DAI104",
            "Βάσεις Δεδομένων",
            "ΠΑΡΑΤΑΣΗ ΗΛΕΚΤΡΟΝΙΚΗΣ ΑΞΙΟΛΟΓΗΣΗΣ ΔΙΔΑΚΤΙΚΟΥ ΕΡΓΟΥ ΕΑΡΙΝΟΥ ΕΞΑΜΗΝΟΥ 2019-2020",
            "https://openeclass.uom.gr/modules/announcements/main_ann.php?aid=15",
            "Σας ενημερώνουμε ότι η ηλεκτρονική αξιολόγηση διδακτικού έργου για το ....",
            1591786714000,
            false
        )}) }
    data.value?.let {
        LazyColumn {
        }
        if (it.isEmpty()) {
            Text(text = "No Announcements Found")
        }
    }
}
@Composable
private fun BottomSheet(announcement: MutableState<Announcement>) {
    Column(Modifier.fillMaxHeight()) {
        Text(text = announcement.value.title)
        Surface(color = MaterialTheme.colors.background, shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(12.dp)) {
            HtmlText(html = announcement.value.description, darkThemeEnabled = isSystemInDarkTheme(), enableLinks = true, modifier = Modifier.padding(8.dp))
        }
        Text(text = SimpleDateFormat.getDateTimeInstance().format(announcement.value.date))
    }
}