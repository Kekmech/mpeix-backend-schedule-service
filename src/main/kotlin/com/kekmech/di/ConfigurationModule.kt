package com.kekmech.di

import com.kekmech.configuration.CacheConfiguration
import com.kekmech.configuration.DatabaseConfiguration
import com.kekmech.helpers.ModuleProvider
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.util.*
import org.koin.dsl.bind

class ConfigurationModule(env: ApplicationEnvironment) : ModuleProvider({
    single {
        env.config
    } bind ApplicationConfig::class

    val cacheDirPath = "application.cache.dir"
    val cacheLimitPath = "application.cache.limit"

    single {
        val config = get<ApplicationConfig>()
        CacheConfiguration(
            dir = config.getString(cacheDirPath),
            limit = config.getLong(cacheLimitPath)
        )
    } bind CacheConfiguration::class

    val dbUrlPath = "application.db.url"
    val dbUserPath = "application.db.user"
    val dbPasswordPath = "application.db.password"

    single {
        val config = get<ApplicationConfig>()
        DatabaseConfiguration(
            url = config.getString(dbUrlPath),
            user = config.getString(dbUserPath),
            password = config.getString(dbPasswordPath)
        )
    } bind DatabaseConfiguration::class
})

@KtorExperimentalAPI
fun ApplicationConfig.getString(path: String) = property(path).getString()

@KtorExperimentalAPI
fun ApplicationConfig.getLong(path: String) = property(path).getString().toLong()
