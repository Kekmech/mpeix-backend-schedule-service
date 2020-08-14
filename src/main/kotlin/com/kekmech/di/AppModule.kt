package com.kekmech.di

import com.google.gson.*
import com.kekmech.di.factories.*
import com.kekmech.helpers.*
import com.kekmech.repository.*
import io.ktor.client.*
import io.netty.util.internal.logging.*
import okhttp3.logging.*
import org.ehcache.*
import org.koin.dsl.*
import java.util.*

class AppModule : ModuleProvider({
    single { GsonFactory.create() } bind Gson::class
    single { HttpClientFactory.create() } bind HttpClient::class
    single { Slf4JLoggerFactory.getInstance("SCHEDULE") } bind InternalLogger::class
    single { Locale.GERMAN } bind Locale::class
    single { CacheFactory.create(get(), get()) } bind CacheManager::class

    factory { ScheduleRepository(get(), get(), get(), get()) } bind ScheduleRepository::class
})

object Logger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) = println(message)
}