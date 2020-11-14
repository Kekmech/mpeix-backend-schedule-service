package com.kekmech.repository.sources

import com.github.benmanes.caffeine.cache.*
import com.google.gson.*
import com.kekmech.*
import com.kekmech.configuration.CacheConfiguration
import com.kekmech.dto.*
import com.kekmech.helpers.*
import com.kekmech.repository.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.netty.util.internal.logging.*
import kotlinx.coroutines.*
import java.io.*
import java.util.concurrent.*

class GroupScheduleSource(
    private val cacheConfiguration: CacheConfiguration,
    private val client: HttpClient,
    private val log: InternalLogger,
    private val gson: Gson,
    private val groupIdSource: DataSource<String, String>
) : DataSource<Key, Schedule>(
    enablePersistentCache = true
) {
    private val executor = Executors.newSingleThreadExecutor()

    override val cache: Cache<Key, Schedule> = Caffeine.newBuilder()
        .maximumSize(cacheConfiguration.limit)
        .expireAfterWrite(48, TimeUnit.HOURS)
        .build()

    override fun getFromRemote(k: Key): Schedule? = runBlocking {
        log.debug("Get schedule from remote: key=$k")
        val groupId = groupIdSource.get(k.groupName)!!
        val start = k.weekStart
        val finish = k.weekStart.plusDays(6)
        client
            .get<MpeiScheduleResponse>("${Endpoint.Mpei.Ruz.schedule}/$groupId") {
                parameter("start", start.formatToMpei())
                parameter("finish", finish.formatToMpei())
            }
            .let { ScheduleMapper.map(k, groupId, it) }
    }

    override fun putToPersistent(k: Key, v: Schedule) = executor.execute {
        log.debug("Put schedule to persistent: key=$k")
        File(cacheConfiguration.dir, k.serialize())
            .writeText(gson.toJson(v))
    }

    override fun getFromPersistent(k: Key): Schedule? {
        return executor.submit<Schedule?> {
            val file = File(cacheConfiguration.dir, k.serialize())
            if (file.exists()) {
                log.debug("Get schedule from persistent: key=$k")
                return@submit gson.fromJson(file.readText(), Schedule::class.java)
            } else {
                return@submit null
            }
        }.get()
    }
}
