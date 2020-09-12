//package com.kekmech.cache
//
//import com.github.benmanes.caffeine.cache.*
//import com.google.gson.*
//import com.kekmech.*
//import com.kekmech.cache.ScheduleCacheWrapper.*
//import com.kekmech.dto.*
//import com.kekmech.helpers.*
//import io.netty.util.internal.logging.*
//import org.ehcache.*
//import org.ehcache.config.*
//import java.io.*
//import java.lang.ref.*
//import java.util.*
//import java.util.function.*
//import kotlin.collections.HashMap
//
//class ScheduleCacheWrapper(
//    private val gson: Gson,
//    private val maxEntries: Int = GlobalConfig.Cache.maxEntriesInRAM,
//    private val expirationRequestCount: Int = GlobalConfig.Cache.expirationRequestCount,
//    private val cacheDir: File = File(GlobalConfig.persistentCacheDir),
//    private val log: InternalLogger
//) : Cache<Key, Schedule> {
//
//    private val map = Caffeine.
//    private val requestFreq = HashMap<Key, Int>()
//    private val mutex = Any()
//
//    init {
//        cacheDir.mkdirs()
//    }
//
//    override fun clear() {
//        map.clear()
//        requestFreq.clear()
//    }
//
//    override fun containsKey(key: Key?) = key?.let(map::containsKey) ?: false
//
//    override fun iterator(): MutableIterator<Cache.Entry<Key, Schedule>> =
//        throw NotImplementedError()
//
//    override fun forEach(action: Consumer<in Cache.Entry<Key, Schedule>>?) =
//        throw NotImplementedError()
//
//    override fun spliterator(): Spliterator<Cache.Entry<Key, Schedule>> =
//        throw NotImplementedError()
//
//    override fun get(key: Key?): Schedule? {
//        val result = synchronized(mutex) { map[key]?.get() ?: getFromFile(key) }
//        if (result != null && key != null) {
//            incrementRequestCountFor(key)
//            if (requestFreq[key]!! > expirationRequestCount) throw ScheduleExpiredByRequestCount(result)
//        }
//        return result
//    }
//
//    private fun getFromFile(key: Key?): Schedule? = try { key
//        ?.let { File(cacheDir, "${it.groupName}_${it.weekOfYear}").takeIf { it.exists() }?.readText() }
//        ?.let { gson.fromJson(it, Schedule::class.java) }
//        ?.also { put(key, it) }
//    } catch (e: Exception) {
//        log.debug("Read file from cache error (key=$key): $e")
//        null
//    }
//
//    override fun getAll(keys: MutableSet<out Key>?): MutableMap<Key, Schedule> =
//        throw NotImplementedError()
//
//    override fun put(key: Key?, value: Schedule?) {
//        if (key == null || value == null) return
//        if (!GlobalConfig.cacheEmptySchedules && value.weeks.isEmpty()) return
//        synchronized(mutex) {
//            map[key] = SoftReference(value)
//            putToFile(key, value)
//            requestFreq[key] = 0
//        }
//    }
//
//    private fun putToFile(key: Key, value: Schedule) =
//        File(cacheDir, "${key.groupName}_${key.weekOfYear}").writeText(gson.toJson(value))
//
//    override fun getRuntimeConfiguration(): CacheRuntimeConfiguration<Key, Schedule> =
//        throw NotImplementedError()
//
//    override fun putAll(entries: MutableMap<out Key, out Schedule>?) =
//        throw NotImplementedError()
//
//    override fun putIfAbsent(key: Key?, value: Schedule?): Schedule? {
//        return if (!containsKey(key)) {
//            put(key, value)
//            null
//        } else {
//            get(key)
//        }
//    }
//
//    override fun remove(key: Key?) {
//        key?.let(map::remove)
//    }
//
//    override fun remove(key: Key?, value: Schedule?): Boolean {
//        return if (containsKey(key) && get(key) == value) {
//            remove(key)
//            true
//        } else {
//            false
//        }
//    }
//
//    override fun removeAll(keys: MutableSet<out Key>?) =
//        throw NotImplementedError()
//
//    override fun replace(key: Key?, value: Schedule?): Schedule? {
//        if (key == null || value == null) return null
//        val oldValue = get(key)
//        if (oldValue != null) put(key, value)
//        return oldValue
//    }
//
//    override fun replace(key: Key?, oldValue: Schedule?, newValue: Schedule?): Boolean {
//        return if (containsKey(key) && get(key) == oldValue) {
//            put(key, newValue)
//            true
//        } else {
//            false
//        }
//    }
//
//
//
//    private fun incrementRequestCountFor(key: Key) = synchronized(mutex) {
//        requestFreq[key] = requestFreq.getOrDefault(key, 0) + 1
//    }
//}