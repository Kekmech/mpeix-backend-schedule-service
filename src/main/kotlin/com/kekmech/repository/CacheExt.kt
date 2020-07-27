package com.kekmech.repository

import org.ehcache.*
import org.ehcache.config.*
import org.ehcache.config.builders.*
import org.ehcache.expiry.*
import java.time.Duration

class CacheManagerConfigContext <K : Any, V : Any>(
    private val kClass: Class<K>,
    private val vClass: Class<V>
) {
    private var resourcePools: ResourcePools? = null
    private var expiryPolicy: ExpiryPolicy<K, V>? = null

    var persistentCacheDir: String? = null

    fun resourcePools(config: ResourcePoolsBuilder.() -> Unit) {
        resourcePools = ResourcePoolsBuilder.newResourcePoolsBuilder()
            .apply(config)
            .build()
    }

    fun timeToLive(init: DurationContext.() -> Duration) {
        expiryPolicy = ExpiryPolicyBuilder
            .timeToLiveExpiration(init(DurationContext())) as ExpiryPolicy<K, V>
    }

    fun timeToIdle(init: DurationContext.() -> Duration) {
        expiryPolicy = ExpiryPolicyBuilder
            .timeToIdleExpiration(init(DurationContext())) as ExpiryPolicy<K, V>
    }

    fun noExpire() {
        expiryPolicy = ExpiryPolicyBuilder.noExpiration() as ExpiryPolicy<K, V>
    }

    fun build() = CacheConfigurationBuilder.newCacheConfigurationBuilder(kClass, vClass) { resourcePools }
        .also { if (expiryPolicy != null) it.withExpiry(expiryPolicy!!) }
        .build()

    class DurationContext {
        val Number.milliseconds get() = Duration.ofMillis(toLong())
        val Number.seconds get() = Duration.ofSeconds(toLong())
        val Number.minutes get() = Duration.ofMinutes(toLong())
        val Number.hours get() = Duration.ofHours(toLong())
        val Number.days get() = Duration.ofDays(toLong())
        val Number.weeks get() = Duration.ofDays(toLong() * 7L)
    }
}

inline fun<reified K : Any, reified V : Any> CacheManagerBuilder<CacheManager>.createCache(
    name: String,
    config: CacheManagerConfigContext<K, V>.() -> Unit
) : CacheManagerBuilder<*> {
    val context = CacheManagerConfigContext(
        K::class.java,
        V::class.java
    ).apply(config)
    return withCache(name, context.build())
        .also {
            if (!context.persistentCacheDir.isNullOrEmpty())
                it.with(CacheManagerBuilder.persistence(context.persistentCacheDir))
        }

}

fun<T : CacheManager> CacheManagerBuilder<T>.asSimpleCacheManager() =
    this as CacheManagerBuilder<CacheManager>

fun<T : CacheManager> CacheManagerBuilder<T>.asPersistentCacheManager() =
    this as CacheManagerBuilder<PersistentCacheManager>

fun newCacheManagerBuilder(): CacheManagerBuilder<CacheManager> =
    CacheManagerBuilder.newCacheManagerBuilder()

inline fun<reified K : Any, reified V : Any> CacheManager.getCache(alias: String) =
    getCache(alias, K::class.java, V::class.java)