package com.geomat.openeclassclient.network


import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber


class HostSelectionInterceptor: Interceptor {

    @Volatile
    private var host: String = "localhost"

    fun setHost(host: String) {
        Timber.i("New Host To Insert: $host")
        if (host.isBlank()) {
            Timber.i("Host is blank. Skipping")
            return
        }
        if (host == "text") {
            Timber.i("Host is \"text\", ie sample text. Skipping")
        }
        this.host = host
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        val newUrl = request.url().toString().replace("localhost", host)

        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }
}