package com.kekmech.di

import com.kekmech.helpers.*
import org.jooq.*
import org.jooq.impl.*
import org.koin.dsl.*
import java.sql.*

object PostgresModule : ModuleProvider({

    val postgresPassword by provideSecret("POSTGRES_PASSWORD")

    fun initPostgreSql(): DSLContext {
        Class.forName("org.postgresql.Driver")
        val connection: Connection = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/mpeix",
            "postgres",
            postgresPassword
        )
        return DSL.using(connection, SQLDialect.POSTGRES)
    }

    single { initPostgreSql() } bind DSLContext::class
})