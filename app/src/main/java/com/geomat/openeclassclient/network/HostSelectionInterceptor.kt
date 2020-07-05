package com.geomat.openeclassclient.network


import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response


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

        //val host = "openeclass.uom.gr"
        Log.i("interceptor", host)
        val newUrl = request.url().newBuilder()
            .host(host)
            .build()

        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }

}