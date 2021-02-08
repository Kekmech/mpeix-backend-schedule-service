package com.kekmech.schedule.di.factories

import com.kekmech.schedule.di.Logger
import com.kekmech.schedule.gson.LocalDateSerializer
import com.kekmech.schedule.gson.LocalTimeSerializer
import com.kekmech.schedule.okhttp.RequiredHeadersInterceptor
import com.kekmech.schedule.okhttp.UnzippingInterceptor
import com.kekmech.schedule.okhttp.trustAllSslCertificates
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.*
import okhttp3.logging.HttpLoggingInterceptor
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object HttpClientFactory {

    fun create() = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                setDateFormat(DateFormat.LONG)
                registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 3000L
        }
        engine {
            addInterceptor(UnzippingInterceptor())
            addInterceptor(RequiredHeadersInterceptor())
            addInterceptor(HttpLoggingInterceptor(Logger).apply {
                setLevel(HttpLoggingInterceptor.Level.HEADERS)
            })

            config {
                followSslRedirects(false)
                followRedirects(false)
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
