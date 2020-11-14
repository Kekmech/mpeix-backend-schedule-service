package com.kekmech.schedule.dto

typealias MpeiScheduleResponse = List<MpeiClasses>

data class MpeiClasses(
    val auditorium: String = "",    // place
    val beginLesson: String = "",
    val endLesson: String = "",
    val date: String = "",
    val discipline: String = "",    // name
    val kindOfWork: String = "",    // type
    val lecturer: String = "",      // person
    val stream: String? = null,
    val group: String? = null
)

typealias MpeiSearchResponse = List<MpeiSearchResult>

data class MpeiSearchResult(
    val id: String = "",
    val label: String = "", // group name
    val description: String = "", // faculty e.t.c.
    val type: String = "" // group|person
)
