package com.kekmech.dto

import java.io.*
import java.time.*

data class Key(
    val groupName: String,
    val weekStart: LocalDate
) : Serializable