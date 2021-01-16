package com.kekmech.schedule.di

import com.google.gson.Gson
import com.kekmech.schedule.di.factories.GsonFactory
import com.kekmech.schedule.di.factories.HttpClientFactory
import com.kekmech.schedule.helpers.ModuleProvider
import com.kekmech.schedule.repository.ScheduleRepository
import com.kekmech.schedule.repository.sources.GroupIdSource
import com.kekmech.schedule.repository.sources.GroupScheduleSource
import com.kekmech.schedule.repository.sources.SessionSource
import io.ktor.client.*
import io.netty.util.internal.logging.InternalLogger
import io.netty.util.internal.logging.Slf4JLoggerFactory
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.bind
import java.util.*

class AppModule : ModuleProvider({
    single { GsonFactory.create() } bind Gson::class
    single { HttpClientFactory.create() } bind HttpClient::class
    single { Slf4JLoggerFactory.getInstance("SCHEDULE") } bind InternalLogger::class
    single { Locale.GERMAN } bind Locale::class

    single { GroupIdSource(get(), get()) } bind GroupIdSource::class
    single { GroupScheduleSource(get(), get(), get(), get(), get<GroupIdSource>()) } bind GroupScheduleSource::class
    single { SessionSource(get(), get(), get()) } bind SessionSource::class
    single {
        ScheduleRepository(get<GroupIdSource>(), get<GroupScheduleSource>(), get<SessionSource>())
    } bind ScheduleRepository::class
})

object Logger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) = println(message)
}
