package com.kekmech.schedule.dto

import java.time.LocalDate

data class GetSessionRequest(
    val groupNumber: String
)

data class GetSessionResponse(
    val items: List<SessionItem>
)

data class SessionItem(
    val name: String = "",
    val type: SessionItemType = SessionItemType.UNDEFINED,
    val place: String = "",
    val person: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: Time = Time()
)

enum class SessionItemType { UNDEFINED, CONSULTATION, EXAM }