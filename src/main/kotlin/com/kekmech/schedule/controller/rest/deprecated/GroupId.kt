package com.kekmech.schedule.controller.rest.deprecated

import com.kekmech.schedule.checkIsValidGroupNumber
import com.kekmech.schedule.dto.GetGroupIdRequest
import com.kekmech.schedule.dto.GetGroupIdResponse
import com.kekmech.schedule.scheduleRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/getGroupId")
class GroupId

fun Route.getGroupId() = location<GroupId> {
    post {
        val groupNumber = call.receive<GetGroupIdRequest>().groupNumber.checkIsValidGroupNumber()
        val groupId = scheduleRepository.getGroupId(groupNumber)
        call.respond(HttpStatusCode.OK, GetGroupIdResponse(groupNumber, groupId))
    }
}
