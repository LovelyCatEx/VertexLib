package com.lovelycatv.vertex.spider.app.bilibili.types

data class BilibiliSpaceVideoSearchResult(
    val mid: Long,
    val page: Page,
    val videos: List<BilibiliVideo>,
) {
    val hasNextPage: Boolean get() = this.page.page * this.page.pageSize < this.page.total

    data class Page(
        val page: Int,
        val pageSize: Int,
        val total: Int,
    )
}
