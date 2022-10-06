package com.geomat.openeclassclient.ui.screens.documents

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.network.*
import com.geomat.openeclassclient.network.dataTransferObjects.parseDocumentPageResponse
import com.geomat.openeclassclient.repository.Credentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.await
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val credentials: Flow<Credentials>,
    private val openEclassService: OpenEclassService
) : ViewModel() {

    var uiState: MutableState<DocumentsState> = mutableStateOf(DocumentsState())
        private set

    private var downloadJob: Job = Job()

    fun refresh(course: Course, id: String) {
        DocumentsState(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = openEclassService.getDocumentsPage(course.id, id).await()
                val list = parseDocumentPageResponse(result)
                uiState.value = DocumentsState(false, list)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun cancelDownload() {
        downloadJob.cancel()
        uiState.value = DocumentsState(false, uiState.value.list, Download.Cancelled())
    }

    fun downloadFile(context: Context, url: String, name: String) {
        downloadJob = viewModelScope.launch {
            try {
                openEclassService.downloadFile(url)
                    .downloadToFileWithProgress(context.filesDir, "temp", getCorrectedFileName(url, name)).collect { download ->
                        uiState.value = DocumentsState(list = uiState.value.list, download = download, loading = false)
                    }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun downloadFileWithDownloadManager (downloadManager: DownloadManager, document: Document) {
        viewModelScope.launch {
            val downloadManagerRequest = DownloadManager.Request(Uri.parse(document.link))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(getCorrectedFileName(document))
                .addRequestHeader(COOKIE_HEADER, TOKEN_IN_COOKIE_PREFIX + credentials.first().token)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,  getCorrectedFileName(document))
            downloadManager.enqueue(downloadManagerRequest)
        }
    }

    fun getCorrectedFileName(document: Document): String {
        return getCorrectedFileName(document.link, document.name)
    }
    fun getCorrectedFileName(url: String, name: String): String {
        val documentName = name.replace("/","-")
        val finalDocumentName = if (documentName.contains(".*\\..{1,4}".toRegex())) documentName else "$documentName.${File(url).extension}"
        Timber.i(finalDocumentName)
        return finalDocumentName
    }

}

data class DocumentsState(
    val loading: Boolean = true,
    val list: List<Document> = emptyList(),
    val download: Download? = null
)

data class Document (
    val isDirectory: Boolean,
    val name: String,
    val id: String,
    val link: String,
)