package com.geomat.openeclassclient.network


import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber


class HostSelectionInterceptor: Interceptor {

    @Volatile
    private var host: String = ""

    fun setHost(inputHost: String) {
        Timber.i("New Host To Insert: $inputHost")
        val newHost = inputHost.replace(Regex("[\r\n\\s]"),"")
        Timber.i("New Host After cleaning: $newHost")
        if (newHost.isBlank()) {
            Timber.i("Host is blank. Skipping")
            return
        }
        if (newHost.startsWith(".")) {
            Timber.i("Host starts with dot. Skipping")
            return
        }
        this.host = newHost
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