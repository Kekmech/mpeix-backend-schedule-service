package com.kekmech.di

import com.kekmech.dto.*
import org.ehcache.*
import org.ehcache.config.builders.*
import kotlin.reflect.*

object EhCacheFactory {

    fun create(): CacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache(SCHEDULE_CACHE, cacheConfig(String::class, Schedule::class, ResourcePoolsBuilder.heap(10)))
        .build(true)

    private fun<K : Any, V : Any> cacheConfig(
        keyType: KClass<K>,
        valueType: KClass<V>,
        resourcePoolsBuilder: ResourcePoolsBuilder
    ) = CacheConfigurationBuilder.newCacheConfigurationBuilder(
        keyType.java,
        valueType.java,
        resourcePoolsBuilder
    )

    const val SCHEDULE_CACHE = "scheduleCache"
}