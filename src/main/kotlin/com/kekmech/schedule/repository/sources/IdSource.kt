package com.kekmech.schedule.repository.sources

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.kekmech.schedule.ExternalException
import com.kekmech.schedule.dto.MpeiSearchResponse
import com.kekmech.schedule.repository.DataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.netty.util.internal.logging.InternalLogger
import kotlinx.coroutines.runBlocking

class IdSource(
    private val client: HttpClient,
    private val log: InternalLogger,
    private val type: String
) : DataSource<String, String>() {

    override val cache: Cache<String, String> = Caffeine.newBuilder()
        .maximumSize(1000)
        .build()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getFromRemote(name: String): String? = runBlocking {
        log.debug("Get $type id from remote: $name")
        val firstSearchResult = client
            .get<MpeiSearchResponse>("http://ts.mpei.ru/api/search") {
                parameter("term", name)
                parameter("type", type)
            }
            .firstOrNull() ?: throw ExternalException("Can't find $type with name $name")
        firstSearchResult.id
    }
}
