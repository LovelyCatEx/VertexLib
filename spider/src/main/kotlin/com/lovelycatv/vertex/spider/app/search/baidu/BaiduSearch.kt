package com.lovelycatv.vertex.spider.app.search.baidu

import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.app.search.AbstractSearchEngine
import com.lovelycatv.vertex.spider.app.search.SearchResult
import com.lovelycatv.vertex.spider.lang.HTMLElement

class BaiduSearch(
    private val spider: VertexSpider,
): AbstractSearchEngine() {
    private val logger = logger()

    override suspend fun search(keyword: String, page: Int): SearchResult {
        val page = if (page <= 0) 1 else page
        val url = "https://www.baidu.com/s?wd=$keyword&pn=${10 * (page - 1)}"
        logger.info("Searching $keyword, currentPage=$page")
        val doc = spider.fetch(url)

        val contentDiv = doc.findElementByXPath(".//div[@id='content_left']")
            ?: return SearchResult.failed(keyword, page, url, "content div not found")
        val resultDivList = contentDiv.findElementsByCssSelector("div.result.c-container")

        val results = resultDivList.mapNotNull {
            val container = it.findElementByCssSelector("div.cosc-card-content")
            container?.let { container ->
                val title = container.findElementByXPath(".//a[contains(@class, 'cosc-title-a') and contains(@class, 'cos-link') and contains(@class, 'cosc-title-md')]")
                val desc = container.findElementByCssSelector("div:nth-child(2)")

                val url = title?.attributes["href"]

                if (title != null && url != null) {
                    SearchResult.Item(
                        title = title.text,
                        description = desc?.text ?: "",
                        url = url,
                    )
                } else {
                    null
                }
            }
        }

        val pageDiv = doc.getElementById("page")
        val nextPage = pageDiv?.let { pageDiv ->
            val inner = pageDiv.findElementByXPath(".//div[contains(@class, 'page-inner')]")?.childNodes ?: emptyList()
            val currentIndex = inner.indexOfFirst { it is HTMLElement && it.tagName == "strong" }
            val nextPage = if (currentIndex + 1 < inner.size) {
                inner[currentIndex + 1].text.toIntOrNull() ?: SearchResult.NO_NEXT_PAGE
            } else SearchResult.NO_NEXT_PAGE

            nextPage
        } ?: SearchResult.NO_NEXT_PAGE

        return SearchResult.success(keyword, page, url, results, nextPage)
    }
}