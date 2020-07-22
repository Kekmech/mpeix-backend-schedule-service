package com.kekmech.dto

data class GetScheduleByGroupRequest(
    val groupNumber: String,
    val weekOffset: Int
)