package com.lovelycatv.vertex.spider.app.search

data class SearchResult(
    val keyword: String,
    val page: Int,
    val url: String,
    val success: Boolean,
    val message: String = "",
    val results: List<Item> = emptyList(),
    val nextPage: Int = NO_NEXT_PAGE,
) {
    val hasNextPage: Boolean get() = nextPage > 0 && nextPage != NO_NEXT_PAGE

    data class Item(
        val title: String,
        val description: String,
        val url: String,
    )

    companion object {
        val NO_NEXT_PAGE = -1

        fun success(keyword: String, page: Int, url: String, results: List<Item>, nextPage: Int): SearchResult {
            return SearchResult(keyword, page, url, true, "", results, nextPage)
        }

        fun failed(keyword: String, page: Int, url: String, message: String): SearchResult {
            return SearchResult(keyword, page, url, false, message)
        }
    }

}