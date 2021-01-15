package com.kekmech.schedule.dto

@Deprecated("Deprecated in MpeiX v1.4 and higher")
data class GetScheduleByGroupRequest(
    val groupNumber: String,
    val weekOffset: Int
)
