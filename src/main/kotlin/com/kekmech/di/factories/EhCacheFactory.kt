package com.kekmech.di.factories

import com.kekmech.dto.*
import com.kekmech.repository.*
import org.ehcache.*
import org.ehcache.config.builders.*
import org.ehcache.config.units.*

object EhCacheFactory {

    fun create(): CacheManager = newCacheManagerBuilder()
        .createScheduleCache()
        .build(true)

    private fun CacheManagerBuilder<CacheManager>.createScheduleCache() =
        createCache<String, Schedule>(SCHEDULE_CACHE) {
            resourcePools {
                disk(500, MemoryUnit.MB)
                heap(120, EntryUnit.ENTRIES)
            }
            timeToLive { 7.days }
            persistentCacheDir = "/ehcache/schedule/"
        }

    const val SCHEDULE_CACHE = "scheduleCache"
}