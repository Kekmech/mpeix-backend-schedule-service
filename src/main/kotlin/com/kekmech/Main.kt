package com.kekmech

import com.kekmech.di.*
import com.kekmech.dto.*
import com.kekmech.gson.*
import com.kekmech.helpers.*
import com.kekmech.repository.*
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.metrics.micrometer.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level
import java.text.*
import java.time.*
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

    routing {
        getGroupId()
        getGroupSchedule()
        healthCheck()
    }

    // Workaround for bug https://youtrack.jetbrains.com/issue/KTOR-1286
    receivePipeline.intercept(ApplicationReceivePipeline.Before) {
        withContext(Dispatchers.IO) {
            proceed()
        }
    }
}

fun Route.getGroupId() = post(Endpoint.getGroupId) {
    val groupNumber =  call.receive<GetGroupIdRequest>().groupNumber.checkIsValidGroupNumber()
    val groupId = scheduleRepository.getGroupId(groupNumber)
    call.respond(HttpStatusCode.OK, GetGroupIdResponse(groupNumber, groupId))
}

fun Route.getGroupSchedule() = post(Endpoint.getGroupSchedule) {
    val request = call.receive<GetScheduleByGroupRequest>()
    val groupNumber = request.groupNumber.checkIsValidGroupNumber()
    val requestedWeekStart = request.weekOffset.let {
        if (it == 0) {
            moscowLocalDate().atStartOfWeek()
        } else {
            moscowLocalDate().plusWeeks(it.toLong()).atStartOfWeek()
        }
    }
    val schedule = scheduleRepository.getSchedule(groupNumber, requestedWeekStart)
    call.respond(HttpStatusCode.OK, schedule)
}

fun Route.healthCheck() = get(Endpoint.health) {
    call.respond(HttpStatusCode.OK, "health")
}
