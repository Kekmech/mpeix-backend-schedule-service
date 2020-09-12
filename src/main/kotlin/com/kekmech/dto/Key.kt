package com.kekmech.dto

import com.kekmech.*
import java.io.*
import java.time.*

data class Key(
    val groupName: String,
    val weekStart: LocalDate
) : Serializable {
    fun serialize(): String = "${groupName}_${weekStart.weekOfYear()}"
}