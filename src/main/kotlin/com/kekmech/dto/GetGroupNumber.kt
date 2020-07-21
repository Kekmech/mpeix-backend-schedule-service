package com.kekmech.dto

data class GetGroupNumberRequest(
    val groupNumber: String? = null
)

data class GetGroupNumberResponse(
    val groupId: String
)