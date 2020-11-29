package com.kekmech.schedule.configuration

data class CacheConfiguration(
    val dir: String,
    val limit: Long
)

data class DatabaseConfiguration(
    val url: String,
    val user: String,
    val password: String
)
