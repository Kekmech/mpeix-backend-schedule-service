package com.kekmech.parser

import com.kekmech.*
import com.kekmech.dto.*
import org.intellij.lang.annotations.*
import org.jsoup.*
import org.jsoup.nodes.*
import java.time.*

/**
 * @param localDate Guideline for the year number and a crutch for the correct date generation for an empty schedule
 */
class ScheduleParser(
    private val localDate: LocalDate = LocalDate.now()
) {

    fun parseWeek(html: String): Week {
        val rowsWithSchedule = Jsoup.parse(html)
            .select("table[class*=mpei-galaktika-lessons-grid-tbl] > tbody > tr")
            .assertUnexpectedBehavior("SCHEDULE_PARSE_ERROR") { !it.isNullOrEmpty() }
        return parseRows(rowsWithSchedule.asIterable())
    }

    private fun parseRows(rowsWithSchedule: Iterable<Element>): Week {
        var day: Day? = null
        val days = mutableListOf<Day>()
        var classes = mutableListOf<Classes>()
        rowsWithSchedule.forEach { row ->
            when {
                row.isClassesGrid() -> {
                    val time = getTimeFromText(
                        row.selectTextOrEmpty("td[class=mpei-galaktika-lessons-grid-time]")
                    )
                    val classesName = row.selectTextOrEmpty("span[class=mpei-galaktika-lessons-grid-name]")
                    val classesType = row.selectTextOrEmpty("span[class=mpei-galaktika-lessons-grid-type]")
                    val classesPlace = row.selectTextOrEmpty("span[class=mpei-galaktika-lessons-grid-room]")
                    val classesGroups = row.selectTextOrEmpty("span[class=mpei-galaktika-lessons-grid-grp]")
                    val classesPerson = row.selectTextOrEmpty("span[class=mpei-galaktika-lessons-grid-pers]")
                    classes.add(Classes(
                        name = classesName,
                        type = getClassesTypeByName(classesType.toUpperCase()),
                        place = classesPlace,
                        groups = classesGroups,
                        person = classesPerson,
                        time = time,
                        number = getNumberByTime(time)
                    ))
                }
                row.isDayGrid() -> {
                    //  if met new day, copy classes to prev day, copy prev day to list, clear classes
                    day?.copy(classes = classes)?.let(days::add)
                    classes = mutableListOf()
                    val dayInfo = row.select("td").text().orEmpty().toUpperCase()
                    val dayOfWeek = getDayOfWeekByName(dayInfo)
                    val monthOfYear = getMonthOfYearByName(dayInfo)
                    val dayOfMonth = getDayOfMonthByName(dayInfo)
                    day = Day(
                        dayOfWeek = dayOfWeek.value,
                        date = localDate.withMonth(monthOfYear.value).withDayOfMonth(dayOfMonth)
                    )
                }
                row.isWeekGrid() -> if (day != null) days += day!! // will never happen
                else -> println("SCHEDULE_PARSE_ERROR: $row")
            }
        }
        day?.copy(classes = classes)?.let(days::add)
        val firstDayOfWeek = days
            .takeIf { it.isNotEmpty() }
            ?.first()?.date?.atStartOfWeek()
            ?: localDate.atStartOfWeek()
        return Week(
            days = days,
            firstDayOfWeek = firstDayOfWeek,
            weekOfYear = firstDayOfWeek.weekOfYear(),
            weekOfSemester = firstDayOfWeek.weekOfSemester()
        )
    }

    @Language("HTML")
    private fun Element.isClassesGrid() = children()
        .first()
        .`is`("td[class=mpei-galaktika-lessons-grid-time]")

    @Language("HTML")
    private fun Element.isDayGrid() = children()
        .first()
        .`is`("td[class=mpei-galaktika-lessons-grid-date]")

    @Language("HTML")
    private fun Element.isWeekGrid() = children()
        .first()
        .`is`("td[class=mpei-galaktika-lessons-grid-week]")

    private fun Element.selectTextOrEmpty(cssQuery: String) = select(cssQuery)?.text().orEmpty()

    private fun getClassesTypeByName(type: String) = when {
        type.contains("ЛЕК") -> ClassesType.LECTURE
        type.contains("ЛАБ") -> ClassesType.LAB
        type.contains("ПРАК") -> ClassesType.PRACTICE
        type.contains("КУРС") -> ClassesType.COURSE
        else -> ClassesType.UNDEFINED
    }

    private fun getTimeFromText(text: String): Time = text
        .split("\\s*-\\s*".toRegex())
        .map { if (it.matches("\\d:\\d{2}".toRegex())) "0$it" else it }
        .let { timeRange ->
        Time(
            start = LocalTime.parse(timeRange.first()),
            end = LocalTime.parse(timeRange.last())
        )
    }

    private fun getNumberByTime(time: Time): Int = when {
        time.start == LocalTime.parse("09:20") -> 1
        time.start == LocalTime.parse("11:10") -> 2
        time.start == LocalTime.parse("13:45") -> 3
        time.start == LocalTime.parse("15:35") -> 4
        time.start > LocalTime.parse("17:10") -> 5
        time.start > LocalTime.parse("18:30") -> 6
        time.start > LocalTime.parse("19:30") -> 7
        else -> -1
    }

    private fun getDayOfWeekByName(day: String): DayOfWeek = when {
        day.contains("ПН") ->   DayOfWeek.MONDAY
        day.contains("ПОН") ->  DayOfWeek.MONDAY
        day.contains("ВТ") ->   DayOfWeek.TUESDAY
        day.contains("СР") ->   DayOfWeek.WEDNESDAY
        day.contains("ЧТ") ->   DayOfWeek.THURSDAY
        day.contains("ЧЕТ") ->  DayOfWeek.THURSDAY
        day.contains("ПТ") ->   DayOfWeek.FRIDAY
        day.contains("ПЯТ") ->  DayOfWeek.FRIDAY
        day.contains("СБ") ->   DayOfWeek.SATURDAY
        day.contains("СУБ") ->  DayOfWeek.SATURDAY
        day.contains("ВС") ->   DayOfWeek.SUNDAY
        day.contains("ВОСК") -> DayOfWeek.SUNDAY
        else -> throw IllegalArgumentException("Something went wrong with days of week")
    }

    private fun getMonthOfYearByName(month: String): Month = when {
        month.contains("СЕН") -> Month.SEPTEMBER
        month.contains("ОКТ") -> Month.OCTOBER
        month.contains("ДЕК") -> Month.DECEMBER
        month.contains("ЯНВ") -> Month.JANUARY
        month.contains("ФЕВ") -> Month.FEBRUARY
        month.contains("МАР") -> Month.MARCH
        month.contains("АПР") -> Month.APRIL
        month.contains("МАЙ") -> Month.MAY
        month.contains("МАЯ") -> Month.MAY
        month.contains("ИЮН") -> Month.JUNE
        month.contains("ИЮЛ") -> Month.JULY
        month.contains("АВГ") -> Month.AUGUST
        else -> throw IllegalArgumentException("Something went wrong with month: $month")
    }

    private fun getDayOfMonthByName(day: String): Int = day.replace("[^0-9]".toRegex(), "").toInt()
}