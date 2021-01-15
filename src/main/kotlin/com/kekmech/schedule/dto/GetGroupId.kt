package com.kekmech.schedule.dto

@Deprecated("Deprecated in MpeiX v1.4 and higher")
data class GetGroupIdRequest(
    val groupNumber: String? = null
)

data class GetGroupIdResponse(
    val groupNumber: String,
    val groupId: String
)
