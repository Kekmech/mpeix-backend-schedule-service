package com.kekmech.schedule.controller.rest.v1

import com.kekmech.schedule.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/v1/person/{personName}/schedule/{offset}")
class PersonSchedule(val personName: String, val offset: Int)

fun Route.getPersonScheduleV1() {
    get<PersonSchedule> { request ->
        val personName = request.personName.checkIsValidPersonName()
        val requestedWeekStart = request.offset.let {
            if (it == 0) {
                moscowLocalDate().atStartOfWeek()
            } else {
                moscowLocalDate().plusWeeks(it.toLong()).atStartOfWeek()
            }
        }
        val schedule = scheduleRepository.getPersonSchedule(personName, requestedWeekStart)
        call.respond(HttpStatusCode.OK, schedule)
    }
}