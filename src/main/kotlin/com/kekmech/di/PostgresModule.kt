package com.kekmech.di

import com.kekmech.helpers.*
import org.jooq.*
import org.jooq.impl.*
import org.koin.dsl.*
import java.sql.*

object PostgresModule : ModuleProvider({

    fun initPostgreSql(): DSLContext {
        val driverClass = Class.forName("org.postgresql.Driver")
        val connection: Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mpeix", "postgres", "tensorflow_sucks")
        return DSL.using(connection, SQLDialect.POSTGRES)
    }

    single { initPostgreSql() } bind DSLContext::class
})