package com.kekmech

import com.kekmech.dto.GetGroupNumberRequest
import com.kekmech.dto.GetGroupNumberResponse
import com.kekmech.okhttp.HeadersInterceptor
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.text.DateFormat

private const val API_BASE_URL = "api.kekmech.com/mpeix/v1/schedule/"

fun main(args: Array<String>) {
    val preconfiguredClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .addInterceptor(HeadersInterceptor())
        .followSslRedirects(false)
        .followRedirects(false)
        .build()

    val client = HttpClient(OkHttp) {
        engine { preconfigured = preconfiguredClient }
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
                val groupNumber = call.receive<GetGroupNumberRequest>().groupNumber
                if (groupNumber == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    val url = "https://mpei.ru/Education/timetable/Pages/default.aspx"
                    val groupId = client
                        .get<HttpResponse>(url) { parameter("group", groupNumber) }
                        .let { it.headers[HttpHeaders.Location].orEmpty() }
                        .let(::Url)
                        .let { it.parameters["groupoid"].orEmpty() }
                    call.respond(HttpStatusCode.OK, GetGroupNumberResponse(groupId))
                }
            }
        }
    }
    server.start(wait = true)
}