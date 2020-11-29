package com.kekmech.schedule.repository

import com.kekmech.schedule.ExternalException
import com.kekmech.schedule.dto.Key
import com.kekmech.schedule.dto.Schedule
import java.time.LocalDate

class ScheduleRepository(
    private val groupIdSource: DataSource<String, String>,
    private val scheduleSource: DataSource<Key, Schedule>
) {

    fun getGroupId(groupNumber: String): String =
        groupIdSource.get(groupNumber) ?: throw ExternalException("Can't get group id from remote")

    fun getSchedule(groupNumber: String, weekStart: LocalDate): Schedule =
        scheduleSource.get(Key(groupNumber, weekStart)) ?: throw ExternalException("Can't get schedule from remote")

}
