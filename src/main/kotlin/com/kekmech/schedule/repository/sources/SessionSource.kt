package com.kekmech.schedule.repository.sources

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.Gson
import com.kekmech.schedule.configuration.CacheConfiguration
import com.kekmech.schedule.dto.Key
import com.kekmech.schedule.dto.SessionItem
import com.kekmech.schedule.repository.DataSource
import com.kekmech.schedule.repository.SessionMapper
import io.netty.util.internal.logging.InternalLogger
import java.io.File
import java.time.LocalDate
import java.time.Month
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SessionSource(
    private val groupScheduleSource: GroupScheduleSource,
    private val cacheConfiguration: CacheConfiguration,
    private val log: InternalLogger,
    private val gson: Gson
) : DataSource<String, List<SessionItem>>() {
    private val executor = Executors.newSingleThreadExecutor()

    override val cache: Cache<String, List<SessionItem>> = Caffeine.newBuilder()
        .maximumSize(cacheConfiguration.limit)
        .expireAfterWrite(48, TimeUnit.HOURS)
        .build()

    override fun getFromRemote(k: String): List<SessionItem>? {
        log.debug("Get session from remote: key=$k")
        // now hardcoded. TODO: make auto detection for dates below
        val sessionDays = listOf(
            LocalDate.of(2021, Month.JANUARY, 4),
            LocalDate.of(2021, Month.JANUARY, 11),
            LocalDate.of(2021, Month.JANUARY, 18),
            LocalDate.of(2021, Month.JANUARY, 25)
        )
        val sessionTimeSchedules = sessionDays.mapNotNull { firstDayOfWeek ->
            groupScheduleSource.get(Key(groupName = k, weekStart = firstDayOfWeek))
        }
        return SessionMapper.map(sessionTimeSchedules)
    }

    override fun putToPersistent(k: String, v: List<SessionItem>) = executor.execute {
        log.debug("Put session to persistent: key=$k")
        File(cacheConfiguration.dir, "${k}_W")
            .writeText(gson.toJson(SessionItemsWrapper(v)))
    }

    override fun getFromPersistent(k: String): List<SessionItem>? {
        return executor.submit<SessionItemsWrapper?> {
            val file = File(cacheConfiguration.dir, "${k}_W")
            if (file.exists()) {
                log.debug("Get schedule from persistent: key=$k")
                return@submit gson.fromJson(file.readText(), SessionItemsWrapper::class.java)
            } else {
                return@submit null
            }
        }.get()?.items
    }

    data class SessionItemsWrapper(val items: List<SessionItem>)
}