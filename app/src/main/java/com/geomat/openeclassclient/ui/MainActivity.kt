package com.geomat.openeclassclient.ui.screens.main

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.geomat.openeclassclient.repository.CredentialsRepository
import com.geomat.openeclassclient.ui.theme.OpenEclassClientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
lateinit var clipboardManager: ClipboardManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var credentialsRepository: CredentialsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            credentialsRepository.credentialsFlow.collect {
                credentialsRepository.setInterceptor()
                runOnUiThread {
                    setContent { OpenEclassClientTheme { OpenEclassApp(it.isLoggedIn) } }
                }
            }
            credentialsRepository.checkTokenStatus()
        }
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    }

    override fun onStart() {
        deleteFiles(applicationContext)
        super.onStart()
    }

    override fun onDestroy() {
        deleteFiles(applicationContext)
        super.onDestroy()
    }

}

private fun deleteFiles(context: Context) {
    // Cached Files in /temp folder
    val tempDir = File(context.filesDir, "/temp")
    var files = tempDir.listFiles()
    if (files != null) {
        for (file: File in files) {
            if (file.lastModified() < System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(60) )
                file.delete()
        }
    }
    // Files download by older versions, TODO: Remove after a while
    files = context.filesDir.listFiles()
    if (files != null) {
        for (file: File in files) {
            if (file.isFile) {
                file.delete()
            }
        }
    }
}

