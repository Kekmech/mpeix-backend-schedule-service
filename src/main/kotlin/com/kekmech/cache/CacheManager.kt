package com.kekmech.cache

import com.google.gson.*
import com.kekmech.dto.*
import org.ehcache.*

class CacheManager(
    private val gson: Gson
) {
    val scheduleCache: Cache<PersistentScheduleCache.Key, Schedule> = PersistentScheduleCache(gson)
}