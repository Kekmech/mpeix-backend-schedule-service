package com.kekmech.schedule.repository.sources

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.Gson
import com.kekmech.schedule.configuration.CacheConfiguration
import com.kekmech.schedule.dto.Key
import com.kekmech.schedule.dto.MpeiScheduleResponse
import com.kekmech.schedule.dto.Schedule
import com.kekmech.schedule.dto.ScheduleType
import com.kekmech.schedule.formatToMpei
import com.kekmech.schedule.repository.DataSource
import com.kekmech.schedule.repository.mappers.ScheduleMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.netty.util.internal.logging.InternalLogger
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScheduleSource(
    private val cacheConfiguration: CacheConfiguration,
    private val client: HttpClient,
    private val log: InternalLogger,
    private val gson: Gson,
    private val idSource: IdSource,
    private val type: String
) : DataSource<Key, Schedule>(
    enablePersistentCache = true
) {
    private val executor = Executors.newSingleThreadExecutor()

    override val cache: Cache<Key, Schedule> = Caffeine.newBuilder()
        .maximumSize(cacheConfiguration.limit)
        .expireAfterWrite(48, TimeUnit.HOURS)
        .build()

    override fun getFromRemote(k: Key): Schedule? = runBlocking {
        log.debug("Get schedule from remote: type=$type; key=$k")
        val id = idSource.get(k.name)!!
        val start = k.weekStart
        val finish = k.weekStart.plusDays(6)
        client
            .get<MpeiScheduleResponse>("http://ts.mpei.ru/api/schedule/$type/$id") {
                parameter("start", start.formatToMpei())
                parameter("finish", finish.formatToMpei())
            }
            .let { ScheduleMapper.map(k,id, it, ScheduleType.valueOf(type.toUpperCase())) }
    }

    override fun putToPersistent(k: Key, v: Schedule) = executor.execute {
        log.debug("Put schedule to persistent: type=$type; key=$k")
        File(cacheConfiguration.dir, k.serialize())
            .writeText(gson.toJson(v))
    }

    override fun getFromPersistent(k: Key): Schedule? {
        return executor.submit<Schedule?> {
            val file = File(cacheConfiguration.dir, k.serialize())
            if (file.exists()) {
                log.debug("Get schedule from persistent: type=$type; key=$k")
                return@submit gson.fromJson(file.readText(), Schedule::class.java)
            } else {
                return@submit null
            }
        }.get()
    }

    override fun clearCache(k: Key) = executor.execute {
        cache.invalidate(k)
        File(cacheConfiguration.dir, k.serialize()).delete()
    }
}
