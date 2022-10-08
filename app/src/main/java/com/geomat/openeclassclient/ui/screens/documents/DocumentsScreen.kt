package com.geomat.openeclassclient.ui.screens.documents

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.R
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.network.Download
import com.geomat.openeclassclient.ui.components.OpenEclassTopBar
import com.geomat.openeclassclient.ui.screens.destinations.DocumentScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber
import java.io.File

@Destination
@Composable
fun DocumentScreen(
    viewModel: DocumentsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    course: Course,
    id: String = "",
    folderName: String = "",
) {
    Scaffold(
        topBar = {
            OpenEclassTopBar(
                title = folderName.ifEmpty { stringResource(id = R.string.tool_docs) },
                navigator = navigator,
                navigateBack = true
            )
        }
    ) { paddingValues ->
        val context = LocalContext.current
        val downloadManager= context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        viewModel.refresh(course, id)
        DocumentsScreenContent(
            viewModel.uiState, 
            onClick = {
                if (it.isDirectory) {
                    navigator.navigate(DocumentScreenDestination(course, it.id, it.name))
                } else {
                    viewModel.downloadFile(context, it.link, name = it.name)
                }}, 
            onCancel = {
                viewModel.cancelDownload()
            },
            onDownload = {
                viewModel.downloadFileWithDownloadManager(downloadManager, it)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }

}
@Composable
fun DocumentsScreenContent(uiState: MutableState<DocumentsState>, onClick: (document: Document) -> Unit = {}, onDownload: (document: Document) -> Unit = {}, onCancel: () -> Unit = {}, modifier: Modifier) {
    val context = LocalContext.current
    if (uiState.value.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
    uiState.value.download.let {
        when (it) {
            is Download.Progress -> {
                LoadingDialog(percent = it.percent) {
                    onCancel()
                }
            }
            is Download.Finished -> {
                openFile(context, it.file)
            }
            else -> {}
        }
    }
    uiState.value.list.let { 
        LazyColumn(modifier = modifier
            .fillMaxSize()
            .animateContentSize()) {
            items(it) {
                DocumentItem(document = it, onDownload = {
                    onDownload(it)
                }, modifier = Modifier
                    .clickable { onClick(it) }
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp))
                Divider(thickness = 1.dp)
            }
        }
    }
    if (uiState.value.list.isEmpty()) {
        Text(text = stringResource(id = R.string.no_results_found))
    }
}

@Composable
fun DocumentItem(document: Document, modifier: Modifier, onDownload: (document: Document) ->Unit = {}) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = if (document.isDirectory) Icons.Default.FolderOpen else Icons.Default.InsertDriveFile, contentDescription = "", modifier = Modifier.padding(horizontal = 8.dp), tint = if (document.isDirectory) Color(0xFFcf9c11) else MaterialTheme.colors.primary)
        Text(text = document.name, style = TextStyle(fontSize = 18.sp), modifier = Modifier.weight(1f))
        AnimatedVisibility(visible = !document.isDirectory) {
            IconButton(onClick = {onDownload(document)}, Modifier.size(28.dp) ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colors.onBackground)
            }
        }
    }
}

@Composable
fun LoadingDialog(percent: Int, onClick: () -> Unit) {
    Dialog(
        onDismissRequest = onClick,
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colors.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 36.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = percent / 100f, modifier = Modifier.size(160.dp), strokeWidth = 6.dp)
                Text(text = "${percent}%", style = TextStyle(fontSize = 28.sp))
            }
            Button(onClick = onClick, modifier = Modifier
                .padding(top = 36.dp)
                .width(160.dp)) {
                Text(text = "Cancel")
            }
        }
    }
}

fun openFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file
    )
    val intent = Intent()
    Timber.i(uri.toString())
    intent.action = Intent.ACTION_VIEW
    intent.data = uri
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(intent)
}