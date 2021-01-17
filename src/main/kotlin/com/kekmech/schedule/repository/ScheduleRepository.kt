package com.kekmech.schedule.repository

import com.kekmech.schedule.ExternalException
import com.kekmech.schedule.dto.*
import com.kekmech.schedule.repository.sources.IdSource
import com.kekmech.schedule.repository.sources.ScheduleSource
import com.kekmech.schedule.repository.sources.SearchSource
import com.kekmech.schedule.repository.sources.SessionSource
import java.time.LocalDate

class ScheduleRepository(
    private val groupIdSource: IdSource,
    private val groupScheduleSource: ScheduleSource,
    private val personScheduleSource: ScheduleSource,
    private val groupSessionSource: SessionSource,
    private val personSessionSource: SessionSource,
    private val groupSearchSource: SearchSource,
    private val personSearchSource: SearchSource
) {

    fun getGroupId(groupNumber: String): String =
        groupIdSource.get(groupNumber) ?: throw ExternalException("Can't get group id from remote")

    fun getGroupSchedule(groupNumber: String, weekStart: LocalDate): Schedule =
        groupScheduleSource.get(Key(groupNumber, weekStart)) ?: throw ExternalException("Can't get schedule from remote")

    fun getPersonSchedule(personName: String, weekStart: LocalDate): Schedule =
        personScheduleSource.get(Key(personName, weekStart)) ?: throw ExternalException("Can't get schedule from remote")

    fun getGroupSession(groupNumber: String): List<SessionItem> =
        groupSessionSource.get(groupNumber) ?: throw ExternalException("Can't get session from remote")

    fun getPersonSession(personName: String): List<SessionItem> =
        personSessionSource.get(personName) ?: throw ExternalException("Can't get session from remote")

    fun getSearchResults(query: String, type: String?): List<SearchResult> = when(type) {
        ScheduleType.GROUP.raw -> groupSearchSource.get(query).orEmpty()
        ScheduleType.PERSON.raw -> personSearchSource.get(query).orEmpty()
        null -> mutableListOf<SearchResult>().apply {
            addAll(groupSearchSource.get(query).orEmpty())
            addAll(personSearchSource.get(query).orEmpty())
        }
        else -> emptyList()
    }

    fun clearCache(groupOrPersonName: String, weekStart: LocalDate) {
        groupScheduleSource.clearCache(Key(groupOrPersonName, weekStart))
        personScheduleSource.clearCache(Key(groupOrPersonName, weekStart))
    }
}
