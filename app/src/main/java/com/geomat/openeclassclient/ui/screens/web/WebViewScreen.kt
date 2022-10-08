package com.geomat.openeclassclient.ui.screens.web

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.geomat.openeclassclient.network.TOKEN_IN_COOKIE_PREFIX
import com.geomat.openeclassclient.repository.Credentials
import com.geomat.openeclassclient.ui.components.OpenEclassTopBar
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
        WebViewScreenContent(url, credentials.value, Modifier.padding(it))
    }
}

@Destination
@Composable
fun BareWebViewScreen(viewModel: WebViewModel = hiltViewModel(), navigator: DestinationsNavigator, title: String, path: String) {
    val credentials = viewModel.credentials.collectAsState(initial = Credentials())
    Scaffold(
        topBar = { OpenEclassTopBar(title = title, navigator = navigator) }
    ) {
        val url = "https://${credentials.value.serverUrl}/modules/${path}"
        Timber.i("Web Url: $url")
        WebViewScreenContent(url, credentials.value, Modifier.padding(it))
    }
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreenContent(url: String, credentials: Credentials, modifier: Modifier) {
    var webView: WebView? = remember {null}
    val backHandlerEnabled = remember { mutableStateOf(false) }
    Column {
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
                        }, onStart = {
                            loading.value = true
                            backHandlerEnabled.value = canGoBack()
                        })
                    settings.javaScriptEnabled = true
                    CookieManager.getInstance().apply {
                        setAcceptCookie(true)
                        setCookie(credentials.serverUrl, TOKEN_IN_COOKIE_PREFIX+credentials.token)
                    }
                    loadUrl(url)
                    webView = this
                }
            }, update = {
                        webView = it
            }, modifier = Modifier.fillMaxSize())
        }
    }
    BackHandler(enabled = backHandlerEnabled.value) {
        webView?.goBack()
    }
}

class WebViewClient(val handler: (url: String) -> Unit, val onFinished: () -> Unit, val onStart: () -> Unit): WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url.toString()
        handler(url)
        return false
    }

    private val css = """
        .add-gutter{
            padding: 8px;
        }
        
        .title-row {
            display:none;
        }
        
        .footer {
            display:none;
        }
    """

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        loadCSS(css, view)
    }

    private fun loadCSS(css: String, view: WebView?) {
        val code = """javascript:(function() { 
            
                var node = document.createElement('style');
        
                node.type = 'text/css';
                node.innerHTML = '${css}';
        
                document.head.appendChild(node);
             
            })()"""
        view?.loadUrl(code)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onFinished()
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onStart()
    }
}