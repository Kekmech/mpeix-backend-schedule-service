package com.kekmech.di

import com.google.gson.*
import com.kekmech.di.factories.*
import com.kekmech.helpers.*
import com.kekmech.repository.*
import com.kekmech.repository.sources.*
import io.ktor.client.*
import io.netty.util.internal.logging.*
import okhttp3.logging.*
import org.koin.dsl.*
import java.util.*

class AppModule : ModuleProvider({
    single { GsonFactory.create() } bind Gson::class
    single { HttpClientFactory.create() } bind HttpClient::class
    single { Slf4JLoggerFactory.getInstance("SCHEDULE") } bind InternalLogger::class
    single { Locale.GERMAN } bind Locale::class

    single { GroupIdSource(get(), get()) } bind GroupIdSource::class
    single { GroupScheduleSource(get(), get(), get<GroupIdSource>()) } bind GroupScheduleSource::class
    single {
        ScheduleRepository(get<GroupIdSource>(), get<GroupScheduleSource>())
    } bind ScheduleRepository::class
})

object Logger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) = println(message)
}