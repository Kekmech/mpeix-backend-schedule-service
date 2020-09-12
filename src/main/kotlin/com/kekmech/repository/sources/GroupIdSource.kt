package com.kekmech.repository.sources

import com.github.benmanes.caffeine.cache.*
import com.kekmech.*
import com.kekmech.dto.*
import com.kekmech.repository.*
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.*

class GroupIdSource(
    private val client: HttpClient
) : DataSource<String, String> {

    private val cache: Cache<String, String> = Caffeine.newBuilder()
        .maximumSize(1000)
        .build()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun get(groupName: String): String? = cache.get(groupName, ::getFromRemote)

    private fun getFromRemote(groupName: String): String? = runBlocking {
        val firstSearchResult = client
            .get<MpeiSearchResponse>(Endpoint.Mpei.Ruz.search) {
                parameter("term", groupName)
                parameter("type", "group")
            }
            .firstOrNull() ?: throw ExternalException("Can't find group with name $groupName")
        firstSearchResult.id
    }
}