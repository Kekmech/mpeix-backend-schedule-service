package com.kekmech.schedule.controller.rest.v1

import com.kekmech.schedule.atStartOfWeek
import com.kekmech.schedule.checkIsValidGroupNumber
import com.kekmech.schedule.moscowLocalDate
import com.kekmech.schedule.scheduleRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/v1/group/{groupNumber}/schedule/{offset}")
class GroupSchedule(val groupNumber: String, val offset: Int)

fun Route.getGroupScheduleV1() {
    get<GroupSchedule> { request ->
        val groupNumber = request.groupNumber.checkIsValidGroupNumber()
        val requestedWeekStart = request.offset.let {
            if (it == 0) {
                moscowLocalDate().atStartOfWeek()
            } else {
                moscowLocalDate().plusWeeks(it.toLong()).atStartOfWeek()
            }
        }
        val schedule = scheduleRepository.getSchedule(groupNumber, requestedWeekStart)
        call.respond(HttpStatusCode.OK, schedule)
    }
}
