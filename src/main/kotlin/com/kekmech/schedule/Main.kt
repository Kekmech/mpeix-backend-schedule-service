package com.kekmech.schedule

import com.kekmech.schedule.controller.restModule
import com.kekmech.schedule.di.AppModule
import com.kekmech.schedule.di.ConfigurationModule
import com.kekmech.schedule.di.PostgresModule
import com.kekmech.schedule.gson.LocalDateSerializer
import com.kekmech.schedule.gson.LocalDateTimeSerializer
import com.kekmech.schedule.gson.LocalTimeSerializer
import com.kekmech.schedule.helpers.modules
import com.kekmech.schedule.repository.ScheduleRepository
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

val scheduleRepository by inject(ScheduleRepository::class.java)

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging) {
        level = Level.INFO
        callIdMdc("requestId")
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
        }
    }
    install(StatusPages) {
        exception<ExternalException> { cause ->
            call.respond(HttpStatusCode.ServiceUnavailable, cause.message.orEmpty())
        }
        exception<LogicException> { cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message.orEmpty())
        }
        exception<ValidationException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.orEmpty())
        }
        exception<Exception> { cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message.orEmpty())
        }
    }
    install(Koin) {
        printLogger()
        modules(AppModule(), PostgresModule, ConfigurationModule(environment))
    }
    install(CallId) {
        generate { UUID.randomUUID().toString() }
        verify { it.isNotEmpty() }
        header("REQUEST_ID")
    }
    install(MicrometerMetrics) {
        registry = SimpleMeterRegistry()
        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .percentiles(0.9, 0.99)
            .build()
    }
}
