package com.kekmech.helpers

object GlobalConfig {

    val port = System.getenv("PORT")?.toIntOrNull() ?: 8081
    val persistentCacheDir = System.getenv("CACHE_DIR") ?: "/etc/ehcache/schedule"
    val cacheEmptySchedules = System.getenv("CACHE_EMPTY_SCHEDULES") == "true"

    object DB {
        val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/mpeix"
        val username = System.getenv("DB_USER") ?: "mpeix"
        val password = System.getenv("DB_PASSWORD") ?: "mpeix"
    }

    object Cache {
        val maxEntriesInRAM = System.getenv("CACHE_MAX_ENTRIES")?.toLongOrNull() ?: 120L
        val expirationRequestCount = System.getenv("CACHE_EXPIRATION_REQUEST_COUNT")?.toIntOrNull() ?: 30
    }
}
