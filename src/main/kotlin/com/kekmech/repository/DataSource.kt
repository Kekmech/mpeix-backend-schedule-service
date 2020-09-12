package com.kekmech.repository

interface DataSource<K : Any, V : Any> {
    fun get(k: K): V?
}