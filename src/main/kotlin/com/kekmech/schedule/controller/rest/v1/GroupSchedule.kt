package com.kekmech.schedule.controller.rest.v1

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/v1/group/{groupNumber}/schedule/{offset}")
class GroupSchedule(val groupNumber: String, val offset: Int)

fun Route.getGroupScheduleV1() {
    get<GroupSchedule> { groupSchedule ->
        call.respond(HttpStatusCode.OK)
    }
}
