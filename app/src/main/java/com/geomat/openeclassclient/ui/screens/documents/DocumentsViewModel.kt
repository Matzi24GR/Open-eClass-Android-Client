package com.geomat.openeclassclient.ui.screens.documents

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.network.Download
import com.geomat.openeclassclient.network.OpenEclassService
import com.geomat.openeclassclient.network.dataTransferObjects.parseDocumentPageResponse
import com.geomat.openeclassclient.network.downloadToFileWithProgress
import com.geomat.openeclassclient.repository.Credentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.await
import timber.log.Timber
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
            credentials.collect {
                try {
                    val result = openEclassService.getDocumentsPage(course.id, id).await()
                    val list = parseDocumentPageResponse(result)
                    uiState.value = DocumentsState(false, list)
                } catch (e: Exception) {
                    Timber.e(e)
                }

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
                    .downloadToFileWithProgress(context.filesDir, "temp", name).collect { download ->
                        uiState.value = DocumentsState(list = uiState.value.list, download = download, loading = false)
                    }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
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