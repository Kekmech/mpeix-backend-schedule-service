package com.kekmech.schedule.repository.sources.search

import com.kekmech.schedule.dto.SearchResult
import com.kekmech.schedule.dto.SearchResultType
import org.jooq.DSLContext

class DatabaseSearchResultsSource(private val dsl: DSLContext) {

    init { createTableIfNeeded() }

    private fun createTableIfNeeded() {
        dsl.fetch("""
            create table if not exists $SEARCH_RESULTS_TABLE_NAME (
                id serial,
                $COLUMN_REMOTE_ID varchar not null,
                $COLUMN_NAME varchar not null UNIQUE,
                $COLUMN_DESCRIPTION varchar not null,
                $COLUMN_TYPE varchar not null
            );
        """.trimIndent())
    }

    fun get(query: String, type: String? = null): List<SearchResult> {
        val clearQuery = query
            .replace("\\s{2,}".toRegex(), " ")
            .trim()
            .replace("[^а-яА-Яa-zA-Z0-9\\-\\s]".toRegex(), "")
        val typeCondition = if (type != null) "and $COLUMN_TYPE = ${type.toUpperCase()}" else ""
        val records = dsl.fetch("""
            select * from $SEARCH_RESULTS_TABLE_NAME
            where $COLUMN_NAME like '%$clearQuery%' $typeCondition 
            limit $RESULTS_LIMIT ;
        """.trimIndent())
        return records.map { record ->
            SearchResult(
                name = record[COLUMN_NAME].toString(),
                description = record[COLUMN_DESCRIPTION].toString(),
                type = record[COLUMN_TYPE]!!.toString().let { SearchResultType.valueOf(it.toUpperCase()) },
                id = record[COLUMN_REMOTE_ID].toString()
            )
        }
    }

    fun put(results: List<SearchResult>) {
        val serializedValues = results.joinToString {
            "('${it.id}', '${it.name}', '${it.description}', '${it.type}')"
        }
        dsl.fetch("""
            insert into $SEARCH_RESULTS_TABLE_NAME ($COLUMN_REMOTE_ID, $COLUMN_NAME, $COLUMN_DESCRIPTION, $COLUMN_TYPE)
            values $serializedValues
            on conflict ($COLUMN_NAME) do update
            set $COLUMN_REMOTE_ID = excluded.$COLUMN_REMOTE_ID ,
                $COLUMN_DESCRIPTION = excluded.$COLUMN_DESCRIPTION ,
                $COLUMN_TYPE = excluded.$COLUMN_TYPE ;
        """.trimIndent())
    }

    companion object {
        private const val SEARCH_RESULTS_TABLE_NAME = "schedule_search_results"
        private const val COLUMN_REMOTE_ID = "remote_id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_TYPE = "type"

        private const val RESULTS_LIMIT = 30
    }
}