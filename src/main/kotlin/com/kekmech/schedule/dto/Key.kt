package com.kekmech.schedule.dto

import com.kekmech.schedule.weekOfYear
import java.io.Serializable
import java.time.LocalDate

data class Key(
    val groupName: String,
    val weekStart: LocalDate
) : Serializable {
    fun serialize(): String = "${groupName}_${weekStart.weekOfYear()}"
}
