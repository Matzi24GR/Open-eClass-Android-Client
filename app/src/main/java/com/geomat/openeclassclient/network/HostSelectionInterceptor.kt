package com.geomat.openeclassclient.network


import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber


class HostSelectionInterceptor: Interceptor {

    @Volatile
    private var host: String = "localhost"

    fun setHost(host: String) {
        this.host = host
    }

    fun HostSelectionInterceptor(host: String) {
        this.host = host
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        if (host.isEmpty()){
            //host = request.url().host()
            Timber.i("Host Empty")
        } else {
            val newUrl = request.url().newBuilder()
                .host(host)
                .build()

            request = request.newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(request)
    }

}