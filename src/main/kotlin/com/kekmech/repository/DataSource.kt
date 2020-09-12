package com.kekmech.repository

import com.github.benmanes.caffeine.cache.*

abstract class DataSource<K : Any, V : Any>(
    private val enablePersistentCache: Boolean = false
) {

    abstract val cache: Cache<K, V>

    open fun get(k: K): V? = cache.get(k, ::getFromRemoteInternal)

    private fun getFromRemoteInternal(k: K): V? {
        return if (enablePersistentCache) {
            val persistentData = getFromPersistent(k)
            if (persistentData == null) {
                val remoteData = getFromRemote(k)
                if (remoteData != null) putToPersistent(k, remoteData)
                remoteData
            } else {
                persistentData
            }
        } else {
            getFromRemote(k)
        }
    }

    abstract fun getFromRemote(k: K): V?

    open fun getFromPersistent(k: K): V? = null

    open fun putToPersistent(k: K, v: V) = Unit
}