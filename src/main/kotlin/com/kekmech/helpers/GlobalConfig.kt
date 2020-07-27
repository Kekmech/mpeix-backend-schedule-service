package com.kekmech.helpers

object GlobalConfig {

    val port = System.getenv("PORT")?.toIntOrNull() ?: 8081

    object DB {
        val host = System.getenv("DB_HOST_NAME") ?: "localhost"
        val name = System.getenv("DB_NAME") ?: "mpeix"
        val user = System.getenv("DB_USER") ?: "postgres"
        val password = System.getenv("DB_PASSWORD") ?: "kek"
    }
}