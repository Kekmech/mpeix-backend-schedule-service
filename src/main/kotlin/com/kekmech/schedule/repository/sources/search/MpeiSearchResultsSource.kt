package com.kekmech.schedule.repository.sources.search

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.kekmech.schedule.configuration.CacheConfiguration
import com.kekmech.schedule.dto.MpeiSearchResponse
import com.kekmech.schedule.dto.SearchResult
import com.kekmech.schedule.repository.DataSource
import com.kekmech.schedule.repository.mappers.SearchResultsMapper
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.netty.util.internal.logging.InternalLogger
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

private val GROUP_NUMBER_PATTERN = "[а-яА-Я]+-[а-яА-Я0-9]+-[0-9]+".toRegex()

class MpeiSearchResultsSource(
    private val client: HttpClient,
    private val cacheConfiguration: CacheConfiguration,
    private val log: InternalLogger
) : DataSource<String, List<SearchResult>>() {

    override val cache: Cache<String, List<SearchResult>> = Caffeine.newBuilder()
        .maximumSize(cacheConfiguration.limit)
        .expireAfterWrite(24, TimeUnit.HOURS)
        .build()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getFromRemote(name: String): List<SearchResult>? = runBlocking {
        val type = when {
            name.matches(GROUP_NUMBER_PATTERN) -> "group"
            else -> "person"
        }
        log.debug("Get $type search result from remote: $name")
        val searchResults = client
            .get<MpeiSearchResponse>("http://ts.mpei.ru/api/search") {
                parameter("term", name)
                parameter("type", type)
                timeout { requestTimeoutMillis = 3000L }
            }
        return@runBlocking SearchResultsMapper.map(searchResults)
    }
}