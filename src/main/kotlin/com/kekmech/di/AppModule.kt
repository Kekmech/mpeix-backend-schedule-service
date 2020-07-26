package com.kekmech.di

import com.kekmech.helpers.*
import com.kekmech.okhttp.*
import com.kekmech.repository.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.netty.util.internal.logging.*
import okhttp3.*
import okhttp3.logging.*
import org.ehcache.*
import org.koin.dsl.*
import java.util.*
import java.util.concurrent.*

object AppModule : ModuleProvider({
    single {
        HttpClient(OkHttp) {
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
    } bind HttpClient::class

    single { Slf4JLoggerFactory.getInstance("*") } bind InternalLogger::class
    single { Locale.forLanguageTag("ru_RU") } bind Locale::class
    factory { GroupsRepository(get(), get(), get()) } bind GroupsRepository::class
    single { EhCacheFactory.create() } bind CacheManager::class
})

object Logger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) = println(message)
}