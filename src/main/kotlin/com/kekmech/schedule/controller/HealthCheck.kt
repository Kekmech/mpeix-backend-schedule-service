package com.kekmech.schedule.controller

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/health")
class HealthCheck

fun Route.healthCheck() = location<HealthCheck> {
    get {
        call.respond(HttpStatusCode.OK, "health")
    }
}
