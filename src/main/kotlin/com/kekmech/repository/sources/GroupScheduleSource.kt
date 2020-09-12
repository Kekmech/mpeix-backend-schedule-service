package com.kekmech.repository.sources

import com.github.benmanes.caffeine.cache.*
import com.kekmech.*
import com.kekmech.dto.*
import com.kekmech.helpers.*
import com.kekmech.repository.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.netty.util.internal.logging.*
import kotlinx.coroutines.*
import java.util.concurrent.*

class GroupScheduleSource(
    private val client: HttpClient,
    private val log: InternalLogger,
    private val groupIdSource: DataSource<String, String>
) : DataSource<Key, Schedule>() {

    override val cache: Cache<Key, Schedule> = Caffeine.newBuilder()
        .maximumSize(GlobalConfig.Cache.maxEntriesInRAM)
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
}