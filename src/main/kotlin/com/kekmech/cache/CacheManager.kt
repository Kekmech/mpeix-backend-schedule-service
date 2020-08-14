package com.kekmech.cache

import com.google.gson.*
import com.kekmech.dto.*
import io.netty.util.internal.logging.*
import org.ehcache.*

class CacheManager(
    private val gson: Gson,
    private val log: InternalLogger
) {
    val scheduleCache: Cache<PersistentScheduleCache.Key, Schedule> = PersistentScheduleCache(gson, log = log)
}