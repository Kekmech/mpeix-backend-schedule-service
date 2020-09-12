package com.kekmech.di.factories

import com.kekmech.di.*
import com.kekmech.gson.*
import com.kekmech.okhttp.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import okhttp3.logging.*
import java.text.*
import java.time.*
import java.util.concurrent.*

object HttpClientFactory {

    fun create() = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                setDateFormat(DateFormat.LONG)
                registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
                registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
            }
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