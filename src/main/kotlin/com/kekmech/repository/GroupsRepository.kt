package com.kekmech.repository

import com.kekmech.*
import com.kekmech.di.EhCacheFactory.SCHEDULE_CACHE
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
import java.time.format.*
import java.time.format.DateTimeFormatter.*

class GroupsRepository(
    private val dsl: DSLContext,
    private val client: HttpClient,
    private val log: InternalLogger,
    private val cacheManager: CacheManager
) {
    private val scheduleCache = cacheManager.getCache(SCHEDULE_CACHE, String::class.java, Schedule::class.java)

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
            ?.also { log.debug("getMpeiScheduleIdFromRemote: $groupNumber -> $it") }

    @Language("SQL")
    private suspend fun insertMpeiScheduleIdToDB(groupNumber: String, mpeiScheduleId: String) =
        dsl.fetch("insert into groups_info (group_number, mpei_schedule_id) values ('$groupNumber', $mpeiScheduleId);")



    suspend fun getSchedule(groupNumber: String, weekStart: LocalDate): Schedule =
        getScheduleFromCache(groupNumber, weekStart)
            ?: getScheduleFromRemote(groupNumber, weekStart)
                .also { insertScheduleToCache(groupNumber, weekStart, it) }

    suspend fun getScheduleFromCache(groupNumber: String, weekStart: LocalDate): Schedule? =
        scheduleCache.get("$groupNumber:${weekStart.format(ISO_LOCAL_DATE)}")
            ?.also { log.debug("getScheduleFromCache: $groupNumber:${weekStart.format(ISO_LOCAL_DATE)}") }

    suspend fun getScheduleFromRemote(groupNumber: String, weekStart: LocalDate): Schedule {
        val mpeiQueryTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
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

    suspend fun insertScheduleToCache(
        groupNumber: String,
        weekStart: LocalDate,
        schedule: Schedule
    ) = scheduleCache.put(
        "$groupNumber:${weekStart.format(ISO_LOCAL_DATE)}",
        schedule
    )
}