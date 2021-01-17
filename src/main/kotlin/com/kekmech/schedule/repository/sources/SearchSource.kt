package com.kekmech.schedule.repository.sources

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.kekmech.schedule.configuration.CacheConfiguration
import com.kekmech.schedule.dto.MpeiSearchResponse
import com.kekmech.schedule.dto.SearchResult
import com.kekmech.schedule.repository.DataSource
import com.kekmech.schedule.repository.mappers.SearchResultsMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.netty.util.internal.logging.InternalLogger
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class SearchSource(
    private val client: HttpClient,
    private val cacheConfiguration: CacheConfiguration,
    private val log: InternalLogger,
    private val type: String
) : DataSource<String, List<SearchResult>>(
    enablePersistentCache = true
) {

    override val cache: Cache<String, List<SearchResult>> = Caffeine.newBuilder()
    .maximumSize(cacheConfiguration.limit)
    .expireAfterWrite(48, TimeUnit.HOURS)
    .build()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getFromRemote(name: String): List<SearchResult>? = runBlocking {
        log.debug("Get $type search result from remote: $name")
        val searchResults = client
            .get<MpeiSearchResponse>("http://ts.mpei.ru/api/search") {
                parameter("term", name)
                parameter("type", type)
            }
        return@runBlocking SearchResultsMapper.map(searchResults)
    }
}