package com.kekmech.schedule.controller

import com.kekmech.schedule.controller.rest.deprecated.getGroupId
import com.kekmech.schedule.controller.rest.deprecated.getGroupSchedule
import com.kekmech.schedule.controller.rest.deprecated.getSession
import com.kekmech.schedule.controller.rest.v1.getGroupScheduleV1
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*

fun Application.restModule() {
    install(Locations)

    routing {
        getGroupId()
        getGroupSchedule()
        getSession()
        // System
        healthCheck()
        // V1
        getGroupScheduleV1()
    }
}
