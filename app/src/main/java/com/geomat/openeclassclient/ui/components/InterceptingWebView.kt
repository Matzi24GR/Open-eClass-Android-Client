package com.geomat.openeclassclient.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.PopUpToBuilder
import com.geomat.openeclassclient.ui.screens.NavGraphs
import com.geomat.openeclassclient.ui.screens.login.externalAuth.ExternalAuthViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CustomWebView(url: String, viewModel: ExternalAuthViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            val domain = url.replace("https://","").replace("http://","").replaceAfter("/","").replace("/","")
            webViewClient = TokenInterceptorWebViewClient(onSuccess = { url ->
                Timber.i("Login Response: $url")
                viewModel.setCredentials(url, domain)
                navigator.navigate(NavGraphs.root) {
                    popUpTo(NavGraphs.root) {
                        PopUpToBuilder()
                    }
                }
            })
            loadUrl(url)
        }
    }, modifier = Modifier.fillMaxSize())
}

class TokenInterceptorWebViewClient(val onSuccess: (url: String) -> Unit): WebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url.toString()
        if (url.contains("eclass-token:")) {
            onSuccess(url)
            CookieManager.getInstance().removeAllCookies {}
            return true
        }
        return false
    }
}