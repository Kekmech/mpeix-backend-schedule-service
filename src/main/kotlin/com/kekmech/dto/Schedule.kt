package com.kekmech.dto

import java.time.*

data class Schedule(
    val groupNumber: String = "",
    val groupId: String = "",
    val weeks: List<Week> = emptyList()
)

data class Week(
    val weekOfYear: Int = 0,
    val weekOFSemester: Int = 0,
    val firstDayOfWeek: LocalDate = LocalDate.now(),
    val days: List<Day> = emptyList()
)

data class Day(
    val dayOfWeek: Int = 0,
    val date: LocalDate = LocalDate.now(),
    val classes: List<Classes> = emptyList()
)

data class Classes(
    val name: String = "",
    val type: ClassesType = ClassesType.UNDEFINED,
    val place: String = "",
    val groups: String = "",
    val person: String = "",
    val time: Time = Time(),
    val number: Int
)

data class Time(
    val start: LocalDateTime = LocalDateTime.now(),
    val end: LocalDateTime = LocalDateTime.now()
)

enum class ClassesType { UNDEFINED, LECTURE, PRACTICE, LAB, COURSE }
