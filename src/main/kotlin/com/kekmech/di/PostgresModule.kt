package com.kekmech.di

import com.kekmech.configuration.DatabaseConfiguration
import com.kekmech.helpers.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.*
import org.jooq.impl.*
import org.koin.dsl.*

object PostgresModule : ModuleProvider({

    fun initPostgreSql(dbConfiguration: DatabaseConfiguration): DSLContext {
        val config = HikariConfig().apply {
            jdbcUrl = dbConfiguration.url
            this.username = dbConfiguration.user
            this.password = dbConfiguration.password
            minimumIdle = 1
            maximumPoolSize = 5
        }
        return DSL.using(HikariDataSource(config), SQLDialect.POSTGRES)
    }

    single { initPostgreSql(get()) } bind DSLContext::class
})
