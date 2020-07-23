package com.kekmech.okhttp

import okhttp3.Interceptor
import okhttp3.Response

class RequiredHeadersInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.request().newBuilder()
            .header("User-Agent", UserAgent.opera)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
            .header("Accept-Language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7")
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Connection", "keep-alive")
            .header("Cache-Control", "no-cache")
            .header("Host", "mpei.ru")
            .build()
            .let(chain::proceed)
}