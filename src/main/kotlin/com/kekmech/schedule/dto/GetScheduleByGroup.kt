package com.kekmech.schedule.dto

data class GetScheduleByGroupRequest(
    val groupNumber: String,
    val weekOffset: Int
)
