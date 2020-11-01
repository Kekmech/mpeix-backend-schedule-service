package com.kekmech.di

import com.kekmech.helpers.*
import com.kekmech.helpers.GlobalConfig.DB
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.*
import org.jooq.impl.*
import org.koin.dsl.*

object PostgresModule : ModuleProvider({

    fun initPostgreSql(): DSLContext {
        val config = HikariConfig().apply {
            jdbcUrl = DB.url
            this.username = DB.username
            this.password = DB.password
            minimumIdle = 1
            maximumPoolSize = 5
        }
        return DSL.using(HikariDataSource(config), SQLDialect.POSTGRES)
    }

    single { initPostgreSql() } bind DSLContext::class
})
