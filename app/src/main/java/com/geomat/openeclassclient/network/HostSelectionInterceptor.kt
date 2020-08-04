package com.geomat.openeclassclient.network


import androidx.core.net.toUri
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber


class HostSelectionInterceptor: Interceptor {

    @Volatile
    private var host: String = "localhost"

    fun setHost(host: String) {
        Timber.i("New Host Selected: $host")
        this.host = host
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        if (host.isBlank()) {
            //host = request.url().host()
            Timber.i("Host Blank")
        } else {
            val newUrl = request.url().toString().replace("localhost", host)

            request = request.newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(request)
    }
}