package com.kekmech.schedule.okhttp

import okhttp3.Interceptor
import okhttp3.Response

class RequiredHeadersInterceptor : Interceptor {
    private val operaUserAgent =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.173"

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.request().newBuilder()
            .header("User-Agent", operaUserAgent)
            .header(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
            )
            .header("Accept-Language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7")
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Connection", "keep-alive")
            .header("Cache-Control", "no-cache")
            .header("Host", "mpei.ru")
            .build()
            .let(chain::proceed)
}
