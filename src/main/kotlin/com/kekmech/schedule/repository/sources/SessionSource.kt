package com.kekmech.schedule.repository.sources

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.kekmech.schedule.configuration.CacheConfiguration
import com.kekmech.schedule.dto.Key
import com.kekmech.schedule.dto.SessionItem
import com.kekmech.schedule.repository.DataSource
import com.kekmech.schedule.repository.SessionMapper
import io.netty.util.internal.logging.InternalLogger
import java.time.LocalDate
import java.time.Month
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SessionSource(
    private val groupScheduleSource: GroupScheduleSource,
    private val cacheConfiguration: CacheConfiguration,
    private val log: InternalLogger
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

    data class SessionItemsWrapper(val items: List<SessionItem>)
}