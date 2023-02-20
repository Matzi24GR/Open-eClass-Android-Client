package com.geomat.openeclassclient.network


import com.google.common.util.concurrent.RateLimiter
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import timber.log.Timber


class AuthInterceptor: Interceptor {

    private val limiter = RateLimiter.create(1.0)

    @Volatile
    private var host: String = ""

    @Volatile
    private var token: String = ""

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

    fun setToken(inputToken: String) {
        if (inputToken.isNotBlank()) token = inputToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        // Change URL
        val newUrl = request.url().toString().replace("localhost", host)
        request = request.newBuilder()
            .url(newUrl)
            .build()

        // Add Token in POST Requests
        if (request.method() == "POST" && request.body().bodyToString().contains(EMPTY_TOKEN_PLACEHOLDER_URLENCODED)) {
            val body = request.body()
            request = request.newBuilder()
                .post(
                    RequestBody.create(
                        body?.contentType(),
                        body.bodyToString()
                            .replace(EMPTY_TOKEN_PLACEHOLDER_URLENCODED, token)
                    )
                )
                .build()
        }

        // Add Token in GET Requests
        if (request.method() == "GET" && request.headers().toString().contains(EMPTY_TOKEN_PLACEHOLDER)) {
            request = request.newBuilder()
                .header(COOKIE_HEADER, TOKEN_IN_COOKIE_PREFIX+token)
                .build()
        }

        if(token.isNotBlank()) limiter.acquire()
        return chain.proceed(request)
    }
}

fun RequestBody?.bodyToString(): String {
    if (this == null) return ""
    val buffer = okio.Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}
