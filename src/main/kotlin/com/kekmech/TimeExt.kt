package com.kekmech

import org.koin.java.KoinJavaComponent.inject
import java.time.*
import java.time.format.*
import java.time.temporal.*
import java.util.*

private val locale by inject(Locale::class.java)

fun LocalDate.atStartOfWeek(): LocalDate = let {
    if (dayOfWeek == DayOfWeek.MONDAY) it else minusDays(dayOfWeek.value - 1L)
}

fun LocalDate.atSaturdayOfWeek(): LocalDate = when (dayOfWeek) {
    DayOfWeek.SATURDAY -> this
    DayOfWeek.SUNDAY -> minusDays(1)
    else -> plusDays(6L - dayOfWeek.value)
}

fun LocalDate.weekOfYear(defaultLocale: Locale = locale): Int = get(WeekFields.of(defaultLocale).weekOfWeekBasedYear())

/**
 * The first day of the fall semester is always the 1th of September, unless the 1th of September is Sunday.
 * The first day of the spring semester is always the first Monday of February.
 * The maximum number of weeks in a semester is 18.
 * Summer time is from July to August. For this date range, and for school weeks greater than 18, -1 will be returned.
 */
fun LocalDate.weekOfSemester(defaultLocale: Locale = locale): Int {
    val weekOfYear = when {
        (month == Month.JULY) -> -1 // summertime
        (month in Month.FEBRUARY..Month.JUNE) -> { // spring semester
            val firstOfFebruary = this
                .withMonth(Month.FEBRUARY.value)
                .withDayOfMonth(1)
            // if 1th of Febryary is Monday, return it's week number, else return next week number
            if (firstOfFebruary.dayOfWeek == DayOfWeek.MONDAY) firstOfFebruary.weekOfYear(defaultLocale)
            else firstOfFebruary.weekOfYear(defaultLocale) + 1
        }
        else -> { // autumn semester
            val firstOfSeptember = this
                .withMonth(Month.SEPTEMBER.value)
                .withDayOfMonth(1)
            // if 1th of February is not Sunday, it's a working day, then return it's week number
            // else return next week number
            if (firstOfSeptember.dayOfWeek != DayOfWeek.SUNDAY) firstOfSeptember.weekOfYear(defaultLocale)
            else firstOfSeptember.weekOfYear(defaultLocale) + 1
        }
    }
    return ((weekOfYear(defaultLocale) - weekOfYear) + 1).let {
        if (it in 1..18) it else -1
    }
}

fun LocalDate.formatToMpei(): String =
    format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

fun String.formatFromMpei(): LocalDate =
    LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy.MM.dd"))

fun moscowLocalTime(): LocalTime {
    return LocalTime.now(ZoneId.of("Europe/Moscow"))
}

fun moscowLocalDate(): LocalDate {
    return LocalDate.now(ZoneId.of("Europe/Moscow"))
}