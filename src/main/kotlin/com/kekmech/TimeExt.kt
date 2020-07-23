package com.kekmech

import org.koin.java.KoinJavaComponent.inject
import java.time.*
import java.time.temporal.*
import java.util.*

private val locale by inject(Locale::class.java)

fun LocalDate.atStartOfWeek(): LocalDate = LocalDate.now().apply {
    minusDays(dayOfWeek.value.toLong())
}

fun LocalDate.weekOfYear(): Int = get(WeekFields.of(locale).weekOfWeekBasedYear())

/**
 * The first day of the fall semester is always the 1th of September, unless the 1th of September is Sunday.
 * The first day of the spring semester is always the first Monday of February.
 * The maximum number of weeks in a semester is 18.
 * Summer time is from July to August. For this date range, and for school weeks greater than 18, -1 will be returned.
 */
fun LocalDate.weekOfSemester(): Int {
    val weekOfYear = when {
        (month in Month.JULY..Month.AUGUST) -> -1 // summertime
        (month in Month.FEBRUARY..Month.JUNE) -> { // spring semester
            val firstOfFebruary = this
                .withMonth(Month.FEBRUARY.value)
                .withDayOfMonth(1)
            // if 1th of Febryary is Monday, return it's week number, else return next week number
            if (firstOfFebruary.dayOfWeek == DayOfWeek.MONDAY) firstOfFebruary.weekOfYear()
            else firstOfFebruary.weekOfYear() + 1
        }
        else -> { // autumn semester
            val firstOfSeptember = this
                .withMonth(Month.SEPTEMBER.value)
                .withDayOfMonth(1)
            // if 1th of February is not Sunday, it's a working day, then return it's week number
            // else return next week number
            if (firstOfSeptember.dayOfWeek != DayOfWeek.SUNDAY) firstOfSeptember.weekOfYear()
            else firstOfSeptember.weekOfYear() + 1
        }
    }
    return ((weekOfYear() - weekOfYear) + 1).let {
        if (it in 1..18) it else -1
    }
}