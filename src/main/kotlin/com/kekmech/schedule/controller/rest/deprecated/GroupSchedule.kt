package com.kekmech.schedule.controller.rest.deprecated

import com.kekmech.schedule.*
import com.kekmech.schedule.dto.GetScheduleByGroupRequest
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/getGroupSchedule")
class GroupSchedule

@Deprecated("Deprecated in MpeiX v1.4 and higher")
fun Route.getGroupSchedule() = location<GroupSchedule> {
    post {
        val request = call.receive<GetScheduleByGroupRequest>()
        val groupNumber = request.groupNumber.checkIsValidGroupNumber()
        val requestedWeekStart = request.weekOffset.let {
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
