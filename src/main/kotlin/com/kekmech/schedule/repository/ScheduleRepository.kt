package com.kekmech.schedule.repository

import com.kekmech.schedule.ExternalException
import com.kekmech.schedule.dto.*
import com.kekmech.schedule.repository.sources.IdSource
import com.kekmech.schedule.repository.sources.ScheduleSource
import com.kekmech.schedule.repository.sources.search.MergedSearchResultsSource
import com.kekmech.schedule.repository.sources.SessionSource
import java.time.LocalDate

class ScheduleRepository(
    private val groupIdSource: IdSource,
    private val groupScheduleSource: ScheduleSource,
    private val personScheduleSource: ScheduleSource,
    private val groupSessionSource: SessionSource,
    private val personSessionSource: SessionSource,
    private val mergedSearchSource: MergedSearchResultsSource
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

    fun getSearchResults(query: String, type: String?): List<SearchResult> {
        val result = mergedSearchSource.get(query).orEmpty()
        return if (type == null) {
            result
        } else {
            result.filter { it.type.name == type.toUpperCase() }
        }.take(10)
    }

    fun clearCache(groupOrPersonName: String, weekStart: LocalDate) {
        groupScheduleSource.clearCache(Key(groupOrPersonName, weekStart))
        personScheduleSource.clearCache(Key(groupOrPersonName, weekStart))
    }
}
