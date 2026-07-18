package com.lovelycatv.vertex.spider.app.bilibili.space

import com.alibaba.fastjson2.JSONObject
import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.app.bilibili.misc.types.BilibiliWbi
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliSpaceVideoSearchResult
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideo
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideoAuthor
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideoStatistics
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideoUGCSeason
import kotlinx.coroutines.delay
import java.net.URLEncoder
import kotlin.time.Duration.Companion.milliseconds

class BilibiliSpaceParser(
    private val spider: VertexSpider
) {
    private val logger = logger()

    suspend fun getAllPublishedVideos(mid: Long, wbi: BilibiliWbi, delay: Long = 250L): BilibiliSpaceVideoSearchResult {
        logger.info("Getting all videos for mid: $mid, wbi=$wbi")

        val resultList = mutableListOf<BilibiliSpaceVideoSearchResult>()

        var currPage = 1
        var curr = getPublishedVideos(mid, wbi, "", currPage)
        resultList.add(curr)

        while (curr.hasNextPage) {
            currPage++
            curr = getPublishedVideos(mid, wbi, "", currPage)
            resultList.add(curr)
            delay(delay.milliseconds)
        }

        return curr.copy(
            videos = resultList.flatMap { it.videos }
        )
    }

    suspend fun getPublishedVideos(mid: Long, wbi: BilibiliWbi, keyword: String, page: Int, pageSize: Int = 40): BilibiliSpaceVideoSearchResult {
        logger.info("Getting videos for mid: $mid, page=$page, pageSize=$pageSize, keyword=$keyword, wbi=$wbi")
        val params = wbi.encryptParams(
            mapOf(
                "pn" to page,
                "ps" to pageSize,
                "web_location" to 233,
                "keyword" to keyword,
                "order" to "pubdate",
                "mid" to mid,
                "platform" to "web",
                "dm_img_str" to "V2ViR0wgMS4wIChPcGVuR0wgRVMgMi4wIENocm9taXVtKQ",
                "dm_cover_img_str" to "QU5HTEUgKEFwcGxlLCBBTkdMRSBNZXRhbCBSZW5kZXJlcjogQXBwbGUgTTQgUHJvLCBVbnNwZWNpZmllZCBWZXJzaW9uKUdvb2dsZSBJbmMuIChBcHBsZS",
                "dm_img_list" to URLEncoder.encode("[]", Charsets.UTF_8),
                "dm_img_inter" to URLEncoder.encode("{\"ds\":[],\"wh\":[],\"of\":[]}", Charsets.UTF_8),
            )
        )
        val url = "https://api.bilibili.com/x/space/wbi/arc/search?$params"

        val json = this.spider.get(url)
        val root = JSONObject.parseObject(json)
        val code = root.getInteger("code") ?: -1
        if (code != 0) {
            throw IllegalStateException("Bilibili API returned code=$code (${root.getString("message")}) for mid=$mid")
        }

        val data = root.getJSONObject("data")
            ?: throw IllegalStateException("Missing data field for mid=$mid")

        val vlist = data.getJSONObject("list")?.getJSONArray("vlist")
        val videos = vlist?.map {
            val item = it as JSONObject

            BilibiliVideo(
                bvId = item.getString("bvid"),
                aid = item.getLong("aid"),
                // The space search list does not expose cid / dimensions.
                cid = -1,
                coverUrl = item.getString("pic"),
                title = item.getString("title"),
                description = item.getString("description"),
                createdTime = item.getLong("created") ?: 0,
                publishedTime = item.getLong("created") ?: 0,
                width = -1,
                height = -1,
                duration = parseDuration(item.getString("length")),
                author = BilibiliVideoAuthor(
                    mid = item.getLong("mid") ?: mid,
                    name = item.getString("author"),
                    avatarUrl = "",
                ),
                statistics = BilibiliVideoStatistics(
                    view = item.getInteger("play") ?: -1,
                    danmaku = item.getInteger("video_review") ?: -1,
                    reply = item.getInteger("comment") ?: -1,
                    favorite = -1,
                    coin = -1,
                    like = -1,
                    share = -1,
                    dislike = -1,
                ),
                ugcSeason = item.getJSONObject("meta")?.let { meta ->
                    BilibiliVideoUGCSeason(
                        id = meta.getLong("id"),
                        title = meta.getString("title"),
                        coverUrl = meta.getString("cover"),
                        mid = meta.getLong("mid"),
                        // The space search meta does not include per-episode sections.
                        sections = emptyList(),
                    )
                },
            )
        } ?: emptyList()

        val pageInfo = data.getJSONObject("page")

        return BilibiliSpaceVideoSearchResult(
            mid = mid,
            page = BilibiliSpaceVideoSearchResult.Page(
                page = pageInfo?.getInteger("pn") ?: page,
                pageSize = pageInfo?.getInteger("ps") ?: pageSize,
                total = pageInfo?.getInteger("count") ?: videos.size,
            ),
            videos = videos,
        )
    }

    /**
     * Parses a duration string such as "08:08" or "1:02:03" into seconds.
     */
    private fun parseDuration(length: String?): Int {
        if (length.isNullOrBlank()) {
            return 0
        }
        return length.split(":")
            .mapNotNull { it.trim().toIntOrNull() }
            .fold(0) { acc, part -> acc * 60 + part }
    }

    companion object {
        fun extractMidFromSpaceUrl(url: String): Long {
            val parts = url.split("/")
            return parts.find { it.matches(Regex("^\\d+$")) }?.toLong()
                ?: throw IllegalArgumentException("Could not extract mid from: $url")
        }
    }
}