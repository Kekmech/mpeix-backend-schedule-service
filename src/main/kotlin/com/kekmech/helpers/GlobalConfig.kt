package com.kekmech.helpers

object GlobalConfig {

    val port = System.getenv("PORT")?.toIntOrNull() ?: 8081
    val persistentCacheDir = System.getenv("CACHE_DIR") ?: "/etc/ehcache/schedule"
    val cacheEmptySchedules = System.getenv("CACHE_EMPTY_SCHEDULES") == "true"

    object DB {
        val host = System.getenv("DB_HOST_NAME") ?: "localhost"
        val name = System.getenv("DB_NAME") ?: "mpeix"
        val user = System.getenv("DB_USER") ?: "postgres"
        val password = System.getenv("DB_PASSWORD") ?: "kek"
    }
}