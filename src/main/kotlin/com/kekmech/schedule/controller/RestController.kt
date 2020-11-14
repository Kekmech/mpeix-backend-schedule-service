package com.kekmech.schedule.controller

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Application.restModule() {
    install(Locations)

    // Workaround for bug https://youtrack.jetbrains.com/issue/KTOR-1286
    receivePipeline.intercept(ApplicationReceivePipeline.Before) {
        withContext(Dispatchers.IO) {
            proceed()
        }
    }

    routing {
        getGroupId()
        getGroupSchedule()
        healthCheck()
    }
}
