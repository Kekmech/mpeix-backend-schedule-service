package com.kekmech.schedule.controller.rest.v1

import com.kekmech.schedule.checkIsValidPersonName
import com.kekmech.schedule.dto.GetSessionResponse
import com.kekmech.schedule.scheduleRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/v1/person/{personName}/session")
class PersonSession(val personName: String)

fun Route.getPersonSessionV1() {
    get<PersonSession> { request ->
        val personName = request.personName.checkIsValidPersonName()
        call.respond(HttpStatusCode.OK, GetSessionResponse(scheduleRepository.getPersonSession(personName)))
    }
}