package com.kekmech.schedule.controller.rest.v1

import com.kekmech.schedule.dto.SearchResponse
import com.kekmech.schedule.scheduleRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.searchV1() {
    get("/v1/search") {
        val (query, type) = call.request.queryParameters.let { it["q"].orEmpty() to it["type"]?.toLowerCase() }
        val searchResults = if (query.isBlank()) {
            emptyList()
        } else {
            scheduleRepository.getSearchResults(query, type)
        }
        call.respond(HttpStatusCode.OK, SearchResponse(searchResults))
    }
}