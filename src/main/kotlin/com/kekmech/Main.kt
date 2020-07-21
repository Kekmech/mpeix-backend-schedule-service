package com.kekmech

import com.kekmech.dto.*
import com.kekmech.okhttp.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
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
import okhttp3.logging.*
import org.intellij.lang.annotations.*
import org.jooq.*
import org.jooq.impl.*
import java.sql.*
import java.text.*

private const val API_BASE_URL = "api.kekmech.com/mpeix/v1/schedule/"

fun main(args: Array<String>) {
    val context = initPostgreSql()

    val client = HttpClient(OkHttp) {
        engine {
            addInterceptor(RequiredHeadersInterceptor())
            addInterceptor(HttpLoggingInterceptor(Logger).apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
        }
        expectSuccess = false
        followRedirects = false
    }

    val server = embeddedServer(Netty, port = 80, host = "127.0.0.1") {
        install(DefaultHeaders)
        install(Compression)
        install(CallLogging)
        install(ContentNegotiation) {
            gson { setDateFormat(DateFormat.LONG) }
        }
        routing {
            post(Endpoint.getGroupId) {
                call.receive<GetGroupIdRequest>().groupNumber?.let { groupNumber ->
                    @Language("SQL")
                    val findInCache =
                        "select mpei_schedule_id from groups_info where (group_number='$groupNumber') limit 1;"
                    val cache = context.fetch(findInCache).firstOrNull()?.getValue("mpei_schedule_id")?.toString()
                    if (cache != null) {
                        println("Get mpei_schedule_id from cache")
                        call.respond(HttpStatusCode.OK, GetGroupIdResponse(cache))
                    } else {
                        println("Get mpei_schedule_id from mpei.ru")
                        val groupId = client
                            .get<HttpResponse>(Mpei.timetableMainPage) { parameter("group", groupNumber) }
                            .let { it.headers[HttpHeaders.Location].orEmpty() }
                            .let(::Url)
                            .let { it.parameters["groupoid"].orEmpty() }

                        @Language("SQL")
                        val insertNewGroupNumber =
                            "insert into groups_info (group_number, mpei_schedule_id) values ('$groupNumber', $groupId);"
                        context.fetch(insertNewGroupNumber)
                        call.respond(HttpStatusCode.OK, GetGroupIdResponse(groupId))
                    }
                } ?: call.respond(HttpStatusCode.BadRequest)
            }

            post(Endpoint.getScheduleByGroup) {
                val groupNumber = call.receive<GetScheduleByGroupRequest>().groupNumber
                context.fetch(SqlRequests.getGroupIdByGroupNumber(groupNumber))
            }
        }
    }
    server.start(wait = true)
}

fun initPostgreSql(): DSLContext {
    val driverClass = Class.forName("org.postgresql.Driver")
    val connection: Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mpeix", "postgres", "tensorflow_sucks")
    return DSL.using(connection, SQLDialect.POSTGRES)
}