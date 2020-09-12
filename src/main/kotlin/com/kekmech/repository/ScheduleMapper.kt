package com.kekmech.repository

import com.kekmech.*
import com.kekmech.dto.*
import java.time.*

object ScheduleMapper {

    fun map(key: Key, groupId: String, input: MpeiScheduleResponse): Schedule? = try {
        val mapOfDays = mutableMapOf<LocalDate, MutableList<Classes>>()
        input.forEach {
            val date = it.date.formatFromMpei()
            val time = getTimeFromText("${it.beginLesson}-${it.endLesson}")
            val list = mapOfDays.getOrDefault(date, mutableListOf())
            list.add(
                Classes(
                    name = it.discipline,
                    type = getClassesTypeByName(it.kindOfWork.toUpperCase()),
                    place = it.auditorium,
                    groups = it.stream ?: it.group ?: "",
                    person = it.lecturer,
                    time = time,
                    number = getNumberByTime(time)
                )
            )
            mapOfDays[date] = list
        }
        val days = mutableListOf<Day>()
        mapOfDays.forEach { (dayOfWeek, classes) ->
            days += Day(
                dayOfWeek = dayOfWeek.dayOfWeek.value,
                date = dayOfWeek,
                classes = classes
            )
        }
        Schedule(
            groupNumber = key.groupName,
            groupId = groupId,
            weeks = listOf(Week(
                weekOfYear = key.weekStart.weekOfYear(),
                weekOfSemester = key.weekStart.weekOfSemester(),
                firstDayOfWeek = key.weekStart,
                days = days
            ))
        )
    } catch (e: Exception) {
        throw LogicException("Can't map remote data to internal data")
    }

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
}