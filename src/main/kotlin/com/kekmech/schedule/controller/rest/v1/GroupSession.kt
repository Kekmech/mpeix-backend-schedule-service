package com.kekmech.schedule.controller.rest.v1

import com.kekmech.schedule.checkIsValidGroupNumber
import com.kekmech.schedule.dto.GetSessionResponse
import com.kekmech.schedule.scheduleRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/v1/group/{groupNumber}/session")
class GroupSession(val groupNumber: String)

fun Route.getGroupSessionV1() {
    get<GroupSession> { request ->
        val groupNumber = request.groupNumber.checkIsValidGroupNumber()
        call.respond(HttpStatusCode.OK, GetSessionResponse(scheduleRepository.getGroupSession(groupNumber)))
    }
}