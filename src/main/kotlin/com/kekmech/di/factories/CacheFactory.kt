package com.kekmech.di.factories

import com.google.gson.*
import com.kekmech.cache.*

object CacheFactory {

    fun create(gson: Gson) = CacheManager(gson)
}