package com.kekmech.cache

import com.google.gson.*
import com.kekmech.*
import com.kekmech.cache.PersistentScheduleCache.*
import com.kekmech.dto.*
import com.kekmech.helpers.*
import org.ehcache.*
import org.ehcache.config.*
import java.io.*
import java.lang.ref.*
import java.util.*
import java.util.function.*
import kotlin.collections.HashMap

class PersistentScheduleCache(
    private val gson: Gson
) : Cache<Key, Schedule> {

    private val maxEntries = GlobalConfig.Cache.maxEntriesInRAM
    private val expirationRequestCount = GlobalConfig.Cache.expirationRequestCount
    private val cacheDir = File(GlobalConfig.persistentCacheDir)
    private val map = object : LinkedHashMap<Key, SoftReference<Schedule>>(maxEntries + 1, 1f) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Key, SoftReference<Schedule>>?) =
            size > maxEntries
    }
    private val requestFreq = HashMap<Key, Int>()

    init {
        cacheDir.mkdirs()
    }

    override fun clear() {
        map.clear()
        requestFreq.clear()
    }

    override fun containsKey(key: Key?) = key?.let(map::containsKey) ?: false

    override fun iterator(): MutableIterator<Cache.Entry<Key, Schedule>> =
        throw NotImplementedError()

    override fun forEach(action: Consumer<in Cache.Entry<Key, Schedule>>?) =
        throw NotImplementedError()

    override fun spliterator(): Spliterator<Cache.Entry<Key, Schedule>> =
        throw NotImplementedError()

    override fun get(key: Key?): Schedule? {
        val result = map[key]?.get() ?: getFromFile(key)
        if (result != null && key != null) {
            requestFreq.countRequestFor(key)
            if (requestFreq[key]!! > expirationRequestCount) throw ScheduleExpiredByRequestCount(result)
        }
        return result
    }

    private fun getFromFile(key: Key?): Schedule? = key
        ?.let { File(cacheDir, "${it.groupName}_${it.weekOfSemester}").takeIf { it.exists() }?.readText() }
        ?.let { gson.fromJson(it, Schedule::class.java) }
        ?.also { put(key, it) }

    override fun getAll(keys: MutableSet<out Key>?): MutableMap<Key, Schedule> =
        throw NotImplementedError()

    override fun put(key: Key?, value: Schedule?) {
        if (key == null || value == null) return
        if (!GlobalConfig.cacheEmptySchedules && value.weeks.isEmpty()) return
        map[key] = SoftReference(value)
        putToFile(key, value)
        requestFreq[key] = 0
    }

    private fun putToFile(key: Key, value: Schedule) =
        File(cacheDir, "${key.groupName}_${key.weekOfSemester}").writeText(gson.toJson(value))

    override fun getRuntimeConfiguration(): CacheRuntimeConfiguration<Key, Schedule> =
        throw NotImplementedError()

    override fun putAll(entries: MutableMap<out Key, out Schedule>?) =
        throw NotImplementedError()

    override fun putIfAbsent(key: Key?, value: Schedule?): Schedule? {
        return if (!containsKey(key)) {
            put(key, value)
            null
        } else {
            get(key)
        }
    }

    override fun remove(key: Key?) {
        key?.let(map::remove)
    }

    override fun remove(key: Key?, value: Schedule?): Boolean {
        return if (containsKey(key) && get(key) == value) {
            remove(key)
            true
        } else {
            false
        }
    }

    override fun removeAll(keys: MutableSet<out Key>?) =
        throw NotImplementedError()

    override fun replace(key: Key?, value: Schedule?): Schedule? {
        if (key == null || value == null) return null
        val oldValue = get(key)
        if (oldValue != null) put(key, value)
        return oldValue
    }

    override fun replace(key: Key?, oldValue: Schedule?, newValue: Schedule?): Boolean {
        return if (containsKey(key) && get(key) == oldValue) {
            put(key, newValue)
            true
        } else {
            false
        }
    }

    data class Key(
        val groupName: String,
        val weekOfSemester: Int
    )

    private fun<K : Any, V : Any> HashMap<K, V>.countRequestFor(key: Key) {
        requestFreq[key] = requestFreq.getOrDefault(key, 0) + 1
    }
}