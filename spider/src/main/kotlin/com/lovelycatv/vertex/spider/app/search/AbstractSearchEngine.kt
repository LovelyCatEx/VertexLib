package com.lovelycatv.vertex.spider.app.search

import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

abstract class AbstractSearchEngine {
    private val logger = logger()

    open suspend fun searchRecursively(keyword: String, maxPages: Int, interval: Long = 2000L): SearchResult {
        logger.info("Preparing search recursively, keyword=$keyword, maxPages=$maxPages, interval=$interval, ETA=${maxPages * (100 + interval)}ms")

        var curPage = 1
        var result = this.search(keyword, curPage)

        val results = mutableListOf(result)

        while (result.hasNextPage && curPage < maxPages) {
            curPage++
            delay(interval.milliseconds)
            result = this.search(keyword, curPage)
            results.add(result)
        }

        logger.info("Search recursively finished, " +
                "keyword=$keyword, " +
                "searchedPages=${results.size}, " +
                "success=${results.count { it.success }}, " +
                "failed=${results.count { !it.success }}"
        )

        return results.lastOrNull()?.copy(
            success = results.any { it.success },
            results = results.flatMap { it.results }
        ) ?: SearchResult.failed(keyword, maxPages, "", "empty result")
    }

    abstract suspend fun search(keyword: String, page: Int): SearchResult
}