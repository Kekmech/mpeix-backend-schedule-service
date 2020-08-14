package com.kekmech.di.factories

import com.google.gson.*
import com.kekmech.cache.*
import io.netty.util.internal.logging.*

object CacheFactory {

    fun create(gson: Gson, log: InternalLogger) = CacheManager(gson, log)
}