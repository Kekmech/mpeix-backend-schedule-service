package com.kekmech.schedule.repository.sources.search

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.kekmech.schedule.configuration.CacheConfiguration
import com.kekmech.schedule.dto.SearchResult
import com.kekmech.schedule.repository.DataSource
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class MergedSearchResultsSource(
    private val cacheConfiguration: CacheConfiguration,
    private val dbSource: DatabaseSearchResultsSource,
    private val mpeiSource: MpeiSearchResultsSource
) : DataSource<String, List<SearchResult>>() {

    override val cache: Cache<String, List<SearchResult>> = Caffeine.newBuilder()
        .maximumSize(cacheConfiguration.limit)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getFromRemote(name: String): List<SearchResult>? = runBlocking {
        val mpeiResults = try {
            mpeiSource.get(name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: emptyList()
        if (mpeiResults.isNotEmpty()) dbSource.put(mpeiResults)
        return@runBlocking dbSource.get(name)
            .sortedBy {
                val index = it.name.indexOf(name, ignoreCase = true)
                if (index == -1) 99999 else index
            }
    }
}