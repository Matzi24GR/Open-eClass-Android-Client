package com.geomat.openeclassclient.ui.components

import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.ui.screens.NavGraphs
import com.geomat.openeclassclient.ui.screens.login.externalAuth.ExternalAuthViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@Composable
fun CustomWebView(url: String, viewModel: ExternalAuthViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            val domain = url.replace("https://","").replace("http://","").replaceAfter("/","").replace("/","")
            webViewClient = TokenInterceptorWebViewClient(onSuccess = { url ->
                Timber.i("Login Response: $url")
                viewModel.setCredentials(url, domain)
                navigator.navigate(NavGraphs.root)
            })
            loadUrl(url)
        }
    })
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