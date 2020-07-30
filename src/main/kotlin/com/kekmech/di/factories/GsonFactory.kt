package com.kekmech.di.factories

import com.google.gson.*
import com.kekmech.gson.*
import java.text.*
import java.time.*

object GsonFactory {
    fun create() = GsonBuilder().apply {
        setDateFormat(DateFormat.LONG)
        registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
        registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
    }.create()
}