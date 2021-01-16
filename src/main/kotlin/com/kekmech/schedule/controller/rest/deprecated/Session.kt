package com.kekmech.schedule.controller.rest.deprecated

import com.kekmech.schedule.checkIsValidGroupNumber
import com.kekmech.schedule.dto.GetSessionRequest
import com.kekmech.schedule.dto.GetSessionResponse
import com.kekmech.schedule.scheduleRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/getSession")
class Session

@Deprecated("Deprecated in MpeiX v1.4 and higher")
fun Route.getSession() = location<Session> {
    post {
        val request = call.receive<GetSessionRequest>()
        val groupNumber = request.groupNumber.checkIsValidGroupNumber()
        call.respond(HttpStatusCode.OK, GetSessionResponse(scheduleRepository.getGroupSession(groupNumber)))
    }
}