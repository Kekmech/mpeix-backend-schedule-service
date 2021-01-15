package com.kekmech.schedule.repository

import com.kekmech.schedule.dto.Schedule
import com.kekmech.schedule.dto.SessionItem
import com.kekmech.schedule.dto.SessionItemType

object SessionMapper {

    fun map(schedules: List<Schedule>): List<SessionItem> {
        return schedules
            .flatMap { it.weeks.first().days }
            .flatMap { day ->
                day.classes.map { classes ->
                    SessionItem(
                        name = classes.name,
                        type = classes.rawType.toSessionItemType(),
                        place = classes.place,
                        person = classes.person,
                        date = day.date,
                        time = classes.time
                    )
                }
            }
    }

    private fun String.toSessionItemType(): SessionItemType = when {
        contains("КОНС") -> SessionItemType.CONSULTATION
        contains("ЭКЗ") -> SessionItemType.EXAM
        else -> SessionItemType.UNDEFINED
    }
}

