package com.kekmech

import org.koin.java.KoinJavaComponent.inject
import java.time.*
import java.time.temporal.*
import java.util.*

private val locale by inject(Locale::class.java)

fun LocalDate.atStartOfWeek() = LocalDate.now().apply {
    minusDays(dayOfWeek.value.toLong())
}

fun LocalDate.weekOfYear() = get(WeekFields.of(locale).weekOfWeekBasedYear())