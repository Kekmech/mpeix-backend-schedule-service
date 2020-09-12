package com.kekmech.cache

import com.google.gson.*
import io.netty.util.internal.logging.*

class CacheManager(
    private val gson: Gson,
    private val log: InternalLogger
) {
    //val scheduleCache: Cache<ScheduleCacheWrapper.Key, Schedule> = ScheduleCacheWrapper(gson, log = log)
}