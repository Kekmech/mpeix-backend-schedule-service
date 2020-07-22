package com.kekmech

import com.kekmech.di.*
import com.kekmech.dto.*
import com.kekmech.gson.*
import com.kekmech.helpers.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.netty.util.internal.logging.*
import org.jooq.*
import org.koin.core.context.*
import org.koin.java.KoinJavaComponent.inject
import java.text.*
import java.time.*

private const val API_BASE_URL = "api.kekmech.com/mpeix/v1/schedule/"

val dsl by inject(DSLContext::class.java)
val client by inject(HttpClient::class.java)
val log by inject(InternalLogger::class.java)

fun main(args: Array<String>) {
    initKoin()

    val server = embeddedServer(Netty, port = 80, host = "127.0.0.1") {
        install(DefaultHeaders)
        install(Compression)
        install(CallLogging)
        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
                registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            }
        }
        install(StatusPages) {
            exception<InvalidArgumentException> { cause ->
                call.respond(HttpStatusCode.BadRequest, cause.message.orEmpty())
            }
            exception<MpeiBackendUnexpectedBehaviorException> { cause ->
                call.respond(HttpStatusCode.BadRequest, cause.message.orEmpty())
            }
        }
        routing {
            provideGetGroupId()
            provideGetScheduleByGroupName()
        }
    }
    server.start(wait = true)
}

fun initKoin() = startKoin {
    modules(
        AppModule,
        PostgresModule
    )
}

fun Routing.provideGetGroupId() = post(Endpoint.getGroupId) {
    val groupNumber= call.receive<GetGroupIdRequest>().groupNumber.checkIsValidGroupNumber()
    dsl.getMpeiScheduleIdByGroupNumber(groupNumber)?.let {
        call.respond(HttpStatusCode.OK, GetGroupIdResponse(it))
        log.debug("Get MpeiScheduleId from cache")
    } ?: run {
        val mpeiScheduleId = client
            .get<HttpResponse>(Mpei.Timetable.mainPage) { parameter("group", groupNumber) }
            .checkGroupFound()
            .let { Url(it.headers[HttpHeaders.Location].orEmpty()) }
            .let { it.parameters["groupoid"].orEmpty() }
            .assertUnexpectedBehavior { it.isNotEmpty() }

        dsl.insertNewGroupInfo(groupNumber, mpeiScheduleId)
        call.respond(HttpStatusCode.OK, GetGroupIdResponse(mpeiScheduleId))
        log.debug("Get MpeiScheduleId from MPEI backend")
    }
}

fun Routing.provideGetScheduleByGroupName() = post(Endpoint.getScheduleByGroup) {
    val context by inject(DSLContext::class.java)
    val client by inject(HttpClient::class.java)

    val request = call.receive<GetScheduleByGroupRequest>()
    val groupNumber = request.groupNumber.checkIsValidGroupNumber()
    val requestedWeekStart = request.weekOffset

    val mpeiScheduleId: String = dsl.getMpeiScheduleIdByGroupNumber(groupNumber)?.also {
        log.debug("Get MpeiScheduleId from cache")
    } ?: run {
        val mpeiScheduleId = client
            .get<HttpResponse>(Mpei.Timetable.mainPage) { parameter("group", groupNumber) }
            .checkGroupFound()
            .let { Url(it.headers[HttpHeaders.Location].orEmpty()) }
            .let { it.parameters["groupoid"].orEmpty() }
            .assertUnexpectedBehavior { it.isNotEmpty() }

        dsl.insertNewGroupInfo(groupNumber, mpeiScheduleId)
        log.debug("Get MpeiScheduleId from MPEI backend")
        mpeiScheduleId
    }

    val schedule = client.get<String>(Mpei.Timetable.scheduleViewPage) {
        parameter("mode", "list")
        parameter("groupoid", mpeiScheduleId)
        parameter("start", requestedWeekStart)
    }

}