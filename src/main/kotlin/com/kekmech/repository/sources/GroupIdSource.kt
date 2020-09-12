package com.kekmech.repository.sources

import com.github.benmanes.caffeine.cache.*
import com.kekmech.*
import com.kekmech.dto.*
import com.kekmech.repository.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.netty.util.internal.logging.*
import kotlinx.coroutines.*

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