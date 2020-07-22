package com.kekmech.parser

import com.kekmech.*
import com.kekmech.dto.*
import org.intellij.lang.annotations.*
import org.jsoup.*
import org.jsoup.nodes.*
import org.koin.java.KoinJavaComponent.inject
import java.text.*
import java.time.*
import java.time.format.*
import java.time.temporal.*
import java.util.*

class ScheduleParser {
    private val locale by inject(Locale::class.java)

    fun parse(html: String): Week {
        val rowsWithSchedule = Jsoup.parse(html)
            .select("table[class*=mpei-galaktika-lessons-grid-tbl] > tr")
            .assertUnexpectedBehavior { !it.isNullOrEmpty() }
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
                        date = LocalDate.now().withMonth(monthOfYear.value).withDayOfMonth(dayOfMonth)
                    )
                }
                row.isWeekGrid() && day != null -> days += day!! // will never happen
                else -> throw IllegalArgumentException("SCHEDULE_PARSE_ERROR")
            }
        }
        val firstDayOfWeek = days.first().date.atStartOfWeek()
        return Week(
            days = days,
            firstDayOfWeek = firstDayOfWeek,
            weekOfYear = firstDayOfWeek,
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

    private fun getTimeFromText(text: String): Time = text.split("\\s*-\\s*".toRegex()).let { timeRange ->
        Time(
            start = LocalDateTime.parse(timeRange.first(), DateTimeFormatter.ISO_TIME),
            end = LocalDateTime.parse(timeRange.last(), DateTimeFormatter.ISO_TIME)
        )
    }

    private fun getNumberByTime(time: Time): Int = when {
        time.start.isEqual(LocalDateTime.parse("9:20")) -> 1
        time.start.isEqual(LocalDateTime.parse("11:10")) -> 2
        time.start.isEqual(LocalDateTime.parse("13:45")) -> 3
        time.start.isEqual(LocalDateTime.parse("15:35")) -> 4
        time.start > LocalDateTime.parse("17:10") -> 5
        time.start > LocalDateTime.parse("18:30") -> 6
        time.start > LocalDateTime.parse("19:30") -> 7
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
        month.contains("ИЮН") -> Month.JUNE
        month.contains("ИЮЛ") -> Month.JULY
        month.contains("АВГ") -> Month.AUGUST
        else -> throw IllegalArgumentException("Something went wrong with month")
    }

    private fun getDayOfMonthByName(day: String): Int = day.replace("[^0-9]".toRegex(), "").toInt()
}

class HtmlToScheduleParser {
    private val coupleBuilder = CoupleBuilder()
    private val scheduleBuilder = ScheduleBuilder()

    fun parse(html: String): Schedule {
        val table = html.substringAfter("class=\"mpei-galaktika-lessons-form\"")
        "<tr>.*?</tr>".toRegex()
            .findAll(table)
            .iterator()
            .forEach(this::parseTr)
        return scheduleBuilder.build()
    }

    private fun parseTr(matchResult: MatchResult) {
        val group = matchResult.groups[0]!!.value
        when {
            group.matches(WEEK_INFO) -> WEEK_INFO
                .findGroupsIn(group)[1]
                .value
                .let { pushWeekInfo(it) }
            group.matches(DAY_INFO) -> DAY_INFO
                .findGroupsIn(group)
                .first { it.value.matches(DAY_INFO_DATE) }
                .value
                .let { pushDayInfo(it) }
            group.matches(COUPLE_INFO) -> pushCoupleInfo(COUPLE_INFO.findGroupsIn(group))
        }
    }

    private fun pushCoupleInfo(findGroupsIn: List<MatchGroup>) {
        var timeInfo = ""
        var metaInfo = ""
        // тут вообще всего два элемента должно быть всегда
        // в одном из них лежит время пары, в другом подробности
        findGroupsIn.forEach {
            if (it.value.matches(COUPLE_INFO_TIME))
                timeInfo = it.value
            else if (!it.value.matches(COUPLE_INFO))
                metaInfo = it.value
        }
        //println("COUPLE INFO: $timeInfo $metaInfo")
        createCouple(timeInfo, metaInfo)
    }

    private fun createCouple(timeInfo: String, metaInfo: String) {
        val groups = COUPLE_INFO_TIME
            .findGroupsIn(timeInfo)
        coupleBuilder.timeStart = groups[1].value
        coupleBuilder.timeEnd = groups[2].value

        coupleBuilder.name = COUPLE_NAME.findGroupsIn(metaInfo)[1].value
        coupleBuilder.place = COUPLE_PLACE.findGroupsIn(metaInfo)[1].value
        coupleBuilder.teacher = COUPLE_TEACHER.findGroupsIn(metaInfo)[1].value
        val type = COUPLE_TYPE.findGroupsIn(metaInfo)[1].value.toUpperCase()
        coupleBuilder.type = when {
            type.contains("ЛЕК") -> ClassesType.LECTURE
            type.contains("ЛАБ") -> ClassesType.LAB
            type.contains("ПРАК") -> ClassesType.PRACTICE
            type.contains("КУРС") -> ClassesType.COURSE
            else -> ClassesType.UNDEFINED
        }
        coupleBuilder.num = when {
            coupleBuilder.timeStart.contains("9:20") -> 1
            coupleBuilder.timeStart == "11:10" -> 2
            coupleBuilder.timeStart == "13:45" -> 3
            coupleBuilder.timeStart == "15:35" -> 4
            coupleBuilder.timeStart.contains("17") -> 5
            coupleBuilder.timeStart.contains("18") -> 6
            coupleBuilder.timeStart.contains("20") -> 7
            else -> -1
        }
        scheduleBuilder.putCouple(coupleBuilder.build())
    }

    private fun pushWeekInfo(weekInfo: String) {
        if (scheduleBuilder.firstCoupleDay == null) {
            val parser: (String) -> Date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())::parse
            val nextWeek = "start=(\\d+.\\d+.\\d+)".toRegex()
                .findGroupsIn(weekInfo)
                .last { it.value.matches("\\d+.\\d+.\\d+".toRegex()) }
                .let { parser(it.value) }
                .let { Time(it) }
            val currentWeek = nextWeek.getDayWithOffset(-7)
            scheduleBuilder.firstCoupleDay = currentWeek.calendar
            println("FIRST COUPLE DAY: ${currentWeek.dayOfMonth}.${currentWeek.month + 1}.${currentWeek.year}")
        }
    }

    // найден заголовок дня
    @SuppressLint("DefaultLocale")
    private fun pushDayInfo(dayInfo: String) {
        //println("DAY_INFO: $dayInfo")
        coupleBuilder.day = getDayNumByName(dayInfo.toUpperCase())
    }

    private fun getMonthNumByName(month: String): Int = when {
        month.contains("СЕН") -> Calendar.SEPTEMBER
        month.contains("ОКТ") -> Calendar.OCTOBER
        month.contains("ДЕК") -> Calendar.DECEMBER
        month.contains("ЯНВ") -> Calendar.JANUARY
        month.contains("ФЕВ") -> Calendar.FEBRUARY
        month.contains("МАР") -> Calendar.MARCH
        month.contains("АПР") -> Calendar.APRIL
        month.contains("МАЙ") -> Calendar.MAY
        month.contains("ИЮН") -> Calendar.JUNE
        month.contains("ИЮЛ") -> Calendar.JULY
        month.contains("АВГ") -> Calendar.AUGUST
        else -> throw IllegalArgumentException("Something went wrong with month")
    }

    private fun getDayNumByName(day: String): Int = when {
        day.contains("ПН") ->   Calendar.MONDAY
        day.contains("ПОН") ->  Calendar.MONDAY
        day.contains("ВТ") ->   Calendar.TUESDAY
        day.contains("СР") ->   Calendar.WEDNESDAY
        day.contains("ЧТ") ->   Calendar.THURSDAY
        day.contains("ЧЕТ") ->  Calendar.THURSDAY
        day.contains("ПТ") ->   Calendar.FRIDAY
        day.contains("ПЯТ") ->  Calendar.FRIDAY
        day.contains("СБ") ->   Calendar.SATURDAY
        day.contains("СУБ") ->  Calendar.SATURDAY
        day.contains("ВС") ->   Calendar.SUNDAY
        day.contains("ВОСК") -> Calendar.SUNDAY
        else -> throw IllegalArgumentException("Something went wrong with days of week")
    }

    private fun Regex.findGroupsIn(group: String) = this
        .find(group)
        .let { it?.groups?.toMutableList() }!!
        .filterNotNull()

    companion object {
        val WEEK_INFO = ".*<td.+?mpei-galaktika-lessons-grid-week.*?>(.+?)</td>.*".toRegex()
        val DAY_INFO = ".*<td.+?mpei-galaktika-lessons-grid-date.*?>(.+?)</td>.*".toRegex()
        val DAY_INFO_DATE = "([^>]+){2}\\s(\\d+).*?([^<]+)".toRegex()
        val COUPLE_INFO = ".*<td.+?mpei-galaktika-lessons-grid-time.*?>(.+?)</td>.*<td.+?mpei-galaktika-lessons-grid-day.*?>(.+?)</td>.*".toRegex()
        val COUPLE_INFO_TIME = "(\\d+:\\d+).*?(\\d+:\\d+)".toRegex()
        val COUPLE_NAME = ".*?<span.*?name.*?>(.*?)</span>.*".toRegex()
        val COUPLE_TYPE = ".*?<span.*?type.*?>(.*?)</span>.*".toRegex()
        val COUPLE_PLACE = ".*?<span.*?room.*?>(.*?)</span>.*".toRegex()
        val COUPLE_TEACHER = ".*?<span.*?pers.*?>(.*?)</span>.*".toRegex()
    }

    class CoupleBuilder {
        fun build() = ParserCouple(name, teacher, place, timeStart, timeEnd, type, num, day, week)

        var name: String = ""        // название предмета
        var teacher: String = ""     // ФИО препода
        var place: String = ""       // место проведения
        var timeStart: String = ""   // время начала
        var timeEnd: String = ""     // время конца
        var type: ClassesType        // тип занятия
        var num: Int = 0             // номер пары
        var day: Int = 0             // день проведения
        var week: Int = -1           // ODD\EVEN\BOTH
    }

    class ScheduleBuilder {
        private var couples = mutableListOf<ParserCouple>()

        fun build() = ParserSchedule(couples)

        var firstCoupleDay: Calendar? = null

        fun putCouple(couple: ParserCouple) = couples.add(couple)
    }
}