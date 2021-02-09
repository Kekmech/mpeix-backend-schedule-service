package com.kekmech.schedule.controller

import com.kekmech.schedule.controller.rest.deprecated.getGroupId
import com.kekmech.schedule.controller.rest.deprecated.getGroupSchedule
import com.kekmech.schedule.controller.rest.deprecated.getSession
import com.kekmech.schedule.controller.rest.v1.*
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*

@Suppress("UNUSED")
fun Application.restModule() {
    install(Locations)

    routing {
        getGroupId()
        getGroupSchedule()
        //getSession()
        // System
        healthCheck()
        clearCache()
        // V1
        getGroupScheduleV1()
        //getGroupSessionV1()
        getPersonScheduleV1()
        //getPersonSessionV1()
        searchV1()
    }
}
