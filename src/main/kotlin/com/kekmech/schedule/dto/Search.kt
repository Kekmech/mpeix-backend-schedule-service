package com.kekmech.schedule.dto

data class SearchResponse(
    val items: List<SearchResult>
)

data class SearchResult(
    val name: String,
    val description: String,
    val id: String,
    val type: SearchResultType
)

enum class SearchResultType { GROUP, PERSON }