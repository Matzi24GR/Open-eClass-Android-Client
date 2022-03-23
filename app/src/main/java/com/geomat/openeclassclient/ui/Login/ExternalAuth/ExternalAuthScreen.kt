package com.geomat.openeclassclient.ui.Login.ExternalAuth

import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.geomat.openeclassclient.ui.LOGIN_NAV_GRAPH
import com.geomat.openeclassclient.ui.Login.ServerSelect.AuthTypeParcel
import com.geomat.openeclassclient.ui.NavGraphs
import com.geomat.openeclassclient.ui.OpenEclassTopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import timber.log.Timber

@Destination(navGraph = LOGIN_NAV_GRAPH)
@Composable
fun ExternalAuthScreen(authType: AuthTypeParcel, viewModel: ExternalAuthViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    Scaffold(topBar = { OpenEclassTopBar(title = authType.name, navigator = navigator)}) {
        viewModel.authType = authType
        CustomWebView(authType.url, navigator = navigator)
    }
}

@Composable
fun CustomWebView(url: String, viewModel: ExternalAuthViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = TokenInterceptorWebViewClient(onSuccess = { url ->
                Timber.i("Login Response: $url")
                viewModel.setCredentials(url)
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

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    ExternalAuthScreen(authType = AuthTypeParcel("Name","demo.openeclass.com"), navigator =  EmptyDestinationsNavigator)
}