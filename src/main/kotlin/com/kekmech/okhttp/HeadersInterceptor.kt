package com.kekmech.okhttp

import okhttp3.Interceptor
import okhttp3.Response

class HeadersInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.request().newBuilder()
            .header("User-Agent", UserAgent.postman)
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Connection", "keep-alive")
            .header("Cache-Control", "no-cache")
            .header("Host", "mpei.ru")
            .build()
            .let(chain::proceed)
}