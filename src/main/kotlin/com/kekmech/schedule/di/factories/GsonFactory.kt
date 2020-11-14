package com.kekmech.schedule.di.factories

import com.google.gson.GsonBuilder
import com.kekmech.schedule.gson.LocalDateDeserializer
import com.kekmech.schedule.gson.LocalDateSerializer
import com.kekmech.schedule.gson.LocalDateTimeSerializer
import com.kekmech.schedule.gson.LocalTimeSerializer
import kekmech.ru.common_network.gson.LocalTimeDeserializer
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object GsonFactory {
    fun create() = GsonBuilder().apply {
        setDateFormat(DateFormat.LONG)
        registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
        registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
        registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
    }.create()
}
