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
import java.text.*

private const val API_BASE_URL = "api.kekmech.com/mpeix/v1/schedule/"

fun main(args: Array<String>) {
    val client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(false)
                followSslRedirects(false)
            }
            addInterceptor(HttpLoggingInterceptor())
            addInterceptor(HeadersInterceptor())
        }
        expectSuccess = false
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
                call.receive<GetGroupNumberRequest>().groupNumber?.let { groupNumber ->
                    val url = "https://mpei.ru/Education/timetable/Pages/default.aspx"
                    val groupId = client
                        .get<HttpResponse>(url) { parameter("group", groupNumber) }
                        .let { it.headers[HttpHeaders.Location].orEmpty() }
                        .let(::Url)
                        .let { it.parameters["groupoid"].orEmpty() }
                    call.respond(HttpStatusCode.OK, GetGroupNumberResponse(groupId))
                } ?: call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
    server.start(wait = true)
}