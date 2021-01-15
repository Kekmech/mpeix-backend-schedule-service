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
class Session(val groupNumber: String)

fun Route.getSessionV1() {
    get<Session> { request ->
        val groupNumber = request.groupNumber.checkIsValidGroupNumber()
        call.respond(HttpStatusCode.OK, GetSessionResponse(scheduleRepository.getSession(groupNumber)))
    }
}