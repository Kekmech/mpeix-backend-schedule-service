package com.kekmech.di

import com.kekmech.di.factories.*
import com.kekmech.helpers.*
import com.kekmech.repository.*
import io.ktor.application.*
import io.ktor.client.*
import io.netty.util.internal.logging.*
import okhttp3.logging.*
import org.ehcache.*
import org.koin.dsl.*
import java.util.*

class AppModule(application: Application) : ModuleProvider({
    single { HttpClientFactory.create() } bind HttpClient::class
    single { Slf4JLoggerFactory.getInstance("*") } bind InternalLogger::class
    single { Locale.forLanguageTag("ru_RU") } bind Locale::class
    single { EhCacheFactory.create() } bind CacheManager::class
    single { application } bind Application::class

    factory { ScheduleRepository(get(), get(), get(), get()) } bind ScheduleRepository::class
})

object Logger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) = println(message)
}