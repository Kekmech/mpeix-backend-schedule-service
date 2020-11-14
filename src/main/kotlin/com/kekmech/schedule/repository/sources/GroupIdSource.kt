package com.kekmech.schedule.repository.sources

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.kekmech.schedule.Endpoint
import com.kekmech.schedule.ExternalException
import com.kekmech.schedule.dto.MpeiSearchResponse
import com.kekmech.schedule.repository.DataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.netty.util.internal.logging.InternalLogger
import kotlinx.coroutines.runBlocking

class GroupIdSource(
    private val client: HttpClient,
    private val log: InternalLogger
) : DataSource<String, String>() {

    override val cache: Cache<String, String> = Caffeine.newBuilder()
        .maximumSize(1000)
        .build()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getFromRemote(groupName: String): String? = runBlocking {
        log.debug("Get group id from remote: $groupName")
        val firstSearchResult = client
            .get<MpeiSearchResponse>(Endpoint.Mpei.Ruz.search) {
                parameter("term", groupName)
                parameter("type", "group")
            }
            .firstOrNull() ?: throw ExternalException("Can't find group with name $groupName")
        firstSearchResult.id
    }
}
