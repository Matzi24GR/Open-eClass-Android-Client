package com.geomat.openeclassclient.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File

annotation class Json
annotation class Xml

const val EMPTY_TOKEN_PLACEHOLDER = "@#@default@#@"
const val EMPTY_TOKEN_PLACEHOLDER_URLENCODED = "%40%23%40default%40%23%40"
const val COOKIE_HEADER = "Cookie"
const val TOKEN_IN_COOKIE_PREFIX = "PHPSESSID="

sealed class Download {
    data class Progress(val percent: Int) : Download()
    data class Finished(val file: File) : Download()
    data class Cancelled(val msg: String = ""): Download()
}

fun ResponseBody.downloadToFileWithProgress(directory: File, folder: String, filename: String): Flow<Download> = flow {
    emit(Download.Progress(0))

    val file = File(directory, "/$folder/${filename}")

    Timber.i("Cached File: ${file.length()}, ToDownloadFile: ${contentLength()}")

    if (file.length() == contentLength()) {
        emit(Download.Finished(file))
        close()
    } else {
        byteStream().use { inputStream ->
            file.outputStream().use { outputStream ->
                val totalBytes = contentLength()
                val data = ByteArray(8_192)
                var progressBytes = 0L

                while (true) {
                    val bytes = inputStream.read(data)
                    if (bytes == -1) {
                        break
                    }
                    outputStream.write(data, 0, bytes)
                    progressBytes += bytes

                    emit(Download.Progress(percent = ((progressBytes * 100) / totalBytes).toInt()))
                }
            }
        }
        emit(Download.Finished(file))
    }

}.flowOn(Dispatchers.IO).distinctUntilChanged()
