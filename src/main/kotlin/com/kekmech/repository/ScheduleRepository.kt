package com.kekmech.repository

import com.kekmech.*
import com.kekmech.cache.CacheManager
import com.kekmech.cache.PersistentScheduleCache.Key
import com.kekmech.dto.*
import com.kekmech.parser.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.netty.util.internal.logging.*
import org.ehcache.*
import org.intellij.lang.annotations.*
import org.jooq.*
import java.nio.charset.*
import java.time.*
import java.time.format.DateTimeFormatter.*

class ScheduleRepository(
    private val dsl: DSLContext,
    private val client: HttpClient,
    private val log: InternalLogger,
    private val cacheManager: CacheManager
) {
    private val scheduleCache: Cache<Key, Schedule> = cacheManager.scheduleCache

    suspend fun getMpeiScheduleId(groupNumber: String): String =
        getMpeiScheduleIdFromDb(groupNumber)
            ?: getMpeiScheduleIdFromRemote(groupNumber)
                .also { insertMpeiScheduleIdToDB(groupNumber, it) }

    @Language("SQL")
    private suspend fun getMpeiScheduleIdFromDb(groupNumber: String) =
        dsl.fetch("select mpei_schedule_id from groups_info where group_number='$groupNumber' limit 1")
            .firstOrNull()
            ?.getValue("mpei_schedule_id")
            ?.toString()
            ?.also { log.debug("getMpeiScheduleIdFromDb: $groupNumber -> $it") }

    private suspend fun getMpeiScheduleIdFromRemote(groupNumber: String) =
        client
            .get<HttpResponse>(Endpoint.Mpei.Timetable.mainPage) { parameter("group", groupNumber) }
            .checkGroupFound()
            .let { Url(it.headers[HttpHeaders.Location].orEmpty()) }
            .let { it.parameters["groupoid"].orEmpty() }
            .assertUnexpectedBehavior { it.isNotEmpty() }
            .also { log.debug("getMpeiScheduleIdFromRemote: $groupNumber -> $it") }

    @Language("SQL")
    private suspend fun insertMpeiScheduleIdToDB(groupNumber: String, mpeiScheduleId: String) =
        dsl.fetch("insert into groups_info (group_number, mpei_schedule_id) values ('$groupNumber', $mpeiScheduleId);")



    suspend fun getSchedule(groupNumber: String, weekStart: LocalDate): Schedule =
        getScheduleFromCache(groupNumber, weekStart)
            ?: getScheduleFromRemote(groupNumber, weekStart)
                .also { insertScheduleToCache(groupNumber, weekStart, it) }

    private suspend fun getScheduleFromCache(
        groupNumber: String,
        weekStart: LocalDate,
        allowGetExpired: Boolean = false
    ): Schedule? =
        scheduleCache.get(Key(groupNumber, weekStart.weekOfYear()))
            ?.takeIfNotExpired(allowGetExpired)
            ?.also { log.debug("getScheduleFromCache: $groupNumber:${weekStart.weekOfSemester()}") }

    private suspend fun getScheduleFromRemote(groupNumber: String, weekStart: LocalDate): Schedule {
        val mpeiQueryTimeFormatter = ofPattern("yyyy.MM.dd")
        val mpeiScheduleId = getMpeiScheduleId(groupNumber)
        return client.get<HttpResponse>(Endpoint.Mpei.Timetable.scheduleViewPage) {
            parameter("mode", "list")
            parameter("groupoid", mpeiScheduleId)
            parameter("start", weekStart.format(mpeiQueryTimeFormatter))
        }.let {
            val html = it.readText(Charset.forName("utf-8"))
            Schedule(
                groupNumber = groupNumber,
                groupId = mpeiScheduleId,
                weeks = listOf(ScheduleParser(weekStart).parseWeek(html))
            )
        }
    }

    private suspend fun insertScheduleToCache(
        groupNumber: String,
        weekStart: LocalDate,
        schedule: Schedule
    ) = scheduleCache.put(Key(groupNumber, weekStart.weekOfYear()), schedule)

    /**
     * Take schedule if it's first week is equal to weekStart
     */
    private fun Schedule.takeIfNotExpired(allowGetExpired: Boolean): Schedule? {
        if (allowGetExpired) return this
        val scheduleWeek = weeks.firstOrNull()?.weekOfYear ?: return null
        val currentWeek = LocalDate.now().weekOfYear()
        if (scheduleWeek <= currentWeek) {
            log.debug("Schedule week ($scheduleWeek) <= current week ($currentWeek): take this schedule")
            return this
        }
        return null
    }
}