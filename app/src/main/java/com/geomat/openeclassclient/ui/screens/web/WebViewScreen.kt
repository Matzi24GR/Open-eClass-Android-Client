package com.geomat.openeclassclient.ui.screens.web

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.domain.Course
import com.geomat.openeclassclient.domain.Tools
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.ui.screens.main.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@Destination
@Composable
fun WebViewScreen(viewModel: WebViewModel = hiltViewModel(), navigator: DestinationsNavigator, toolName: String, course: Course) {
    val credentials = viewModel.credentials.collectAsState(initial = Credentials())
    val tool = Tools.from(toolName)
    Scaffold(
        topBar = { OpenEclassTopBar(title = if (tool!=null) stringResource(tool.stringResource) else toolName, navigator = navigator) }
    ) {
        val url = "https://${credentials.value.serverUrl}/modules/${tool?.path ?: toolName}/?course=${course.id}"
        Timber.i("Web Url: $url")
        WebViewScreenContent(url, credentials.value)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreenContent(url: String, credentials: Credentials) {
    val loading = remember { mutableStateOf(true) }
    if (loading.value) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    if (credentials.serverUrl.isNotBlank()) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient(
                    handler = { url ->
                        if (url.contains("file.php")) {
                            val intent = Intent(Intent.ACTION_VIEW)
                            val uri = Uri.parse(url)
                            intent.data = uri
                            context.startActivity(intent)
                        }
                    }, onFinished = {
                        loading.value = false
                    })
                settings.javaScriptEnabled = true
                CookieManager.getInstance().apply {
                    setAcceptCookie(true)
                    setCookie(credentials.serverUrl, "PHPSESSID=${credentials.token}")
                }

                loadUrl(url)
            }
        }, modifier = Modifier.fillMaxSize())
    }
}

class WebViewClient(val handler: (url: String) -> Unit, val onFinished: () -> Unit): WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url.toString()
        handler(url)
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.loadUrl("javascript:document.getElementsByClassName(\"row title-row margin-top-thin\")[0].setAttribute(\"style\",\"display:none;\");")
        view?.loadUrl("javascript:document.getElementsByClassName(\"footer\")[0].setAttribute(\"style\",\"display:none;\");")
        onFinished()
    }
}