package com.kekmech.di.factories

import com.kekmech.di.*
import com.kekmech.okhttp.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import okhttp3.logging.*
import java.util.concurrent.*

object HttpClientFactory {

    fun create() = HttpClient(OkHttp) {
        engine {
            addInterceptor(UnzippingInterceptor())
            addInterceptor(RequiredHeadersInterceptor())
            addInterceptor(HttpLoggingInterceptor(Logger).apply {
                setLevel(HttpLoggingInterceptor.Level.HEADERS)
            })

            config {
                followSslRedirects(true)
                followRedirects(true)
                retryOnConnectionFailure(true)
                cache(null)
                connectTimeout(15, TimeUnit.SECONDS)
                readTimeout(15, TimeUnit.SECONDS)
                writeTimeout(15, TimeUnit.SECONDS)
                trustAllSslCertificates()
            }
        }
        expectSuccess = false
        followRedirects = false
    }
}