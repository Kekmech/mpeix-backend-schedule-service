package com.kekmech.di

import com.kekmech.helpers.*
import com.kekmech.okhttp.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.netty.util.internal.logging.*
import okhttp3.logging.*
import org.koin.dsl.*
import java.util.*

object AppModule : ModuleProvider({
    single {
        HttpClient(OkHttp) {
            engine {
                addInterceptor(UnzippingInterceptor())
                addInterceptor(RequiredHeadersInterceptor())
                addInterceptor(HttpLoggingInterceptor(Logger).apply {
                    setLevel(HttpLoggingInterceptor.Level.HEADERS)
                })
            }
            expectSuccess = false
            followRedirects = false
        }
    } bind HttpClient::class

    single { Slf4JLoggerFactory.getInstance("*") } bind InternalLogger::class

    single { Locale.forLanguageTag("ru_RU") } bind Locale::class
})