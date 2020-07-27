package com.kekmech.di

import com.kekmech.helpers.*
import com.kekmech.helpers.GlobalConfig.DB.host
import com.kekmech.helpers.GlobalConfig.DB.name
import com.kekmech.helpers.GlobalConfig.DB.password
import com.kekmech.helpers.GlobalConfig.DB.user
import org.jooq.*
import org.jooq.impl.*
import org.koin.dsl.*
import java.sql.*

object PostgresModule : ModuleProvider({

    fun initPostgreSql(): DSLContext {
        Class.forName("org.postgresql.Driver")
        val connection: Connection = DriverManager.getConnection(
            "jdbc:postgresql://$host:5432/$name", user, password
        )
        return DSL.using(connection, SQLDialect.POSTGRES)
    }

    single { initPostgreSql() } bind DSLContext::class
})