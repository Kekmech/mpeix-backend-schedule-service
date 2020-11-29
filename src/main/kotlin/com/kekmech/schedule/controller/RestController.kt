package com.kekmech.schedule.controller

import com.kekmech.schedule.controller.rest.deprecated.getGroupId
import com.kekmech.schedule.controller.rest.deprecated.getGroupSchedule
import com.kekmech.schedule.controller.rest.v1.getGroupScheduleV1
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
        // System
        healthCheck()
        // V1
        getGroupScheduleV1()
    }
}
