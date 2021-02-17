package com.kekmech.schedule.repository.mappers

import com.kekmech.schedule.dto.MpeiSearchResponse
import com.kekmech.schedule.dto.SearchResult
import com.kekmech.schedule.dto.SearchResultType

object SearchResultsMapper {

    fun map(mpeiSearchResults: MpeiSearchResponse): List<SearchResult> = mpeiSearchResults
        .map {
            SearchResult(
                name = it.label.clear(),
                description = it.description,
                id = it.id,
                type = SearchResultType.valueOf(it.type.toUpperCase())
            )
        }

    private fun String.clear() = this
        .replace("\\s{2,}".toRegex(), " ")
}