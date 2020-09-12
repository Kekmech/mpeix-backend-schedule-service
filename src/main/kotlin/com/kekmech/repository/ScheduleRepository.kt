package com.kekmech.repository

import com.kekmech.dto.*
import com.kekmech.dto.Key
import io.ktor.client.*
import io.netty.util.internal.logging.*
import org.jooq.*
import java.time.*

class ScheduleRepository(
    private val dsl: DSLContext,
    private val client: HttpClient,
    private val log: InternalLogger,
    private val groupIdSource: DataSource<String, String>,
    private val scheduleSource: DataSource<Key, Schedule>
) {

    fun getGroupId(groupNumber: String): String =
        groupIdSource.get(groupNumber) ?: throw ExternalException("Can't get group id from remote")

    fun getSchedule(groupNumber: String, weekStart: LocalDate): Schedule =
        scheduleSource.get(Key(groupNumber, weekStart)) ?: throw ExternalException("Can't get schedule from remote")

}