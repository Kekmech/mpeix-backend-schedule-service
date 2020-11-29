package com.kekmech.schedule.di

import com.kekmech.schedule.configuration.DatabaseConfiguration
import com.kekmech.schedule.helpers.ModuleProvider
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.dsl.bind

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
