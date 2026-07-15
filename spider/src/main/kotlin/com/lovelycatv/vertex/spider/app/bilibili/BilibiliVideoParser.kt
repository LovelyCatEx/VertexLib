package com.lovelycatv.vertex.spider.app.bilibili

import com.alibaba.fastjson2.JSONObject
import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.spider.RequestOptions
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpider
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliPlayerInfo
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideo
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideoAuthor
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideoStatistics
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideoUGCSeason

class BilibiliVideoParser(
    private val jsoupSpider: JsoupSpider
) {
    private val logger = logger()

    suspend fun parseVideo(url: String, options: RequestOptions? = null): BilibiliVideo {
        val bvId = extractBVIdFromUrl(url)
        logger.info("Starting parsing video, bvId=$bvId")
        val json = try {
            jsoupSpider.get("https://api.bilibili.com/x/web-interface/view?bvid=$bvId", options)
        } catch (e: Exception) {
            throw IllegalStateException("Could not get video info for $url, bvId=$bvId", e)
        }


        val root = JSONObject.parseObject(json)
        val code = root.getInteger("code") ?: -1
        if (code != 0) {
            throw IllegalStateException("Bilibili API returned code=$code (${root.getString("message")}) for $url, bvId=$bvId")
        }

        val data = root.getJSONObject("data")
            ?: throw IllegalStateException("Missing data field in video info for $url, bvId=$bvId")

        val dimension = data.getJSONObject("dimension")

        return BilibiliVideo(
            bvId = data.getString("bvid"),
            aid = data.getLong("aid"),
            cid = data.getLong("cid"),
            coverUrl = data.getString("pic"),
            title = data.getString("title"),
            description = data.getString("desc"),
            createdTime = data.getLong("ctime"),
            publishedTime = data.getLong("pubdate"),
            width = dimension?.getInteger("width") ?: 0,
            height = dimension?.getInteger("height") ?: 0,
            duration = data.getInteger("duration") ?: 0,
            author = parseAuthor(data.getJSONObject("owner")),
            statistics = parseStatistics(data.getJSONObject("stat")),
            ugcSeason = parseUGCSeason(data.getJSONObject("ugc_season")),
        )
    }

    suspend fun getPlayerInfo(video: BilibiliVideo, options: RequestOptions? = null): BilibiliPlayerInfo {
        return this.getPlayerInfo(video.bvId, video.cid, options)
    }

    suspend fun getPlayerInfo(bvId: String, cid: Long, options: RequestOptions? = null): BilibiliPlayerInfo {
        logger.info("Starting get video player in, bvId=$bvId, cid=$cid")
        val url = "https://api.bilibili.com/x/player/wbi/playurl?bvid=$bvId&cid=$cid&fnval=4048"
        val json = try {
            jsoupSpider.get(url, options)
        } catch (e: Exception) {
            throw IllegalStateException("Could not get video player info for $url, bvId=$bvId, cid=$cid", e)
        }

        try {
            val data = JSONObject.parseObject(json).getJSONObject("data")
            if (data.containsKey("v_voucher")) {
                throw IllegalStateException("Encountered api restriction, please try again later")
            }

            val supportQualities = data.getJSONArray("support_formats").map {
                val item = it as JSONObject

                BilibiliPlayerInfo.QualityMetadata(
                    id = item.getInteger("quality"),
                    format = item.getString("format"),
                    description = item.getString("new_description"),
                    codecs = item.getJSONArray("codecs").map { it.toString() }
                )
            }.associateBy { it.id }
            val dash = data.getJSONObject("dash")

            val duration = dash.getInteger("duration")
            val qualities = dash.getJSONArray("video").map {
                val item = it as JSONObject

                val id = item.getInteger("id")

                BilibiliPlayerInfo.Video(
                    metadata = supportQualities[id]!!,
                    width = item.getInteger("width"),
                    height = item.getInteger("height"),
                    frameRate = if (item.containsKey("frameRate"))
                        item.getInteger("frameRate")
                    else if (item.containsKey("frame_rate")) {
                        item.getInteger("frame_rate")
                    } else {
                        -1
                    },
                    urls = setOfNotNull(
                        item.getString("baseUrl"),
                        item.getString("base_url"),
                        *item.getJSONArray("backupUrl").map { it.toString() }.toTypedArray(),
                        *item.getJSONArray("backup_url").map { it.toString() }.toTypedArray(),
                    ).toList()
                )
            }.groupBy {
                it.metadata.id
            }.mapValues {
                it.value.first().copy(
                    urls = it.value.flatMap { it.urls }.toSet().toList()
                )
            }

            val audios = dash.getJSONArray("audio").map {
                val item = it as JSONObject

                BilibiliPlayerInfo.Audio(
                    id = item.getInteger("id"),
                    bandwidth = item.getInteger("bandwidth"),
                    urls = setOfNotNull(
                        item.getString("baseUrl"),
                        item.getString("base_url"),
                        *item.getJSONArray("backupUrl").map { it.toString() }.toTypedArray(),
                        *item.getJSONArray("backup_url").map { it.toString() }.toTypedArray(),
                    ).toList(),
                    codecs = item.getString("codecs")
                )
            }.groupBy {
                it.id
            }.mapValues {
                it.value.first().copy(
                    urls = it.value.flatMap { it.urls }.toSet().toList()
                )
            }

            return BilibiliPlayerInfo(
                bvId = bvId,
                cid = cid,
                url = url,
                duration = duration,
                supportQualities = supportQualities.values.toList(),
                videos = qualities,
                audios = audios,
            )
        } catch (e: Exception) {
            throw IllegalStateException("Error parsing video player info for $url, bvId=$bvId, cid=$cid, message=${e.message}, body=$json", e)
        }
    }

    private fun parseAuthor(json: JSONObject): BilibiliVideoAuthor {
        return BilibiliVideoAuthor(
            mid = json.getLong("mid"),
            name = json.getString("name"),
            avatarUrl = json.getString("face"),
        )
    }

    private fun parseStatistics(json: JSONObject): BilibiliVideoStatistics {
        // The top-level stat object uses "favorite" while episode stats use "fav".
        return BilibiliVideoStatistics(
            view = json.getInteger("view") ?: 0,
            danmaku = json.getInteger("danmaku") ?: 0,
            reply = json.getInteger("reply") ?: 0,
            favorite = (json.getInteger("favorite") ?: json.getInteger("fav")) ?: 0,
            coin = json.getInteger("coin") ?: 0,
            like = json.getInteger("like") ?: 0,
            share = json.getInteger("share") ?: 0,
            dislike = json.getInteger("dislike") ?: 0,
        )
    }

    private fun parseUGCSeason(json: JSONObject?): BilibiliVideoUGCSeason? {
        if (json == null) {
            return null
        }

        val sections = json.getJSONArray("sections")?.map {
            val section = it as JSONObject

            val episodes = section.getJSONArray("episodes")?.map { ep ->
                val episode = ep as JSONObject
                val arc = episode.getJSONObject("arc")
                val page = episode.getJSONObject("page")
                val pageDimension = page?.getJSONObject("dimension")

                BilibiliVideoUGCSeason.Section.Episode(
                    bvId = episode.getString("bvid"),
                    aid = episode.getLong("aid"),
                    cid = episode.getLong("cid"),
                    coverUrl = arc?.getString("pic") ?: "",
                    title = episode.getString("title"),
                    createdTime = arc?.getLong("ctime") ?: 0,
                    publishedTime = arc?.getLong("pubdate") ?: 0,
                    width = pageDimension?.getInteger("width") ?: 0,
                    height = pageDimension?.getInteger("height") ?: 0,
                    duration = arc?.getInteger("duration") ?: 0,
                    author = parseAuthor(arc.getJSONObject("author")),
                    statistics = parseStatistics(arc.getJSONObject("stat")),
                )
            } ?: emptyList()

            BilibiliVideoUGCSeason.Section(
                id = section.getLong("id"),
                title = section.getString("title"),
                episodes = episodes,
            )
        } ?: emptyList()

        return BilibiliVideoUGCSeason(
            id = json.getLong("id"),
            title = json.getString("title"),
            coverUrl = json.getString("cover"),
            mid = json.getLong("mid"),
            sections = sections,
        )
    }

    companion object {
        private val URL_PATTERNS = listOf(
            Regex("""^https?://(?:www\.)?bilibili\.com/video/(BV[\w]+|av\d+)""", RegexOption.IGNORE_CASE)
        )

        fun extractBVIdFromUrl(url: String): String? {
            for (pattern in URL_PATTERNS) {
                pattern.find(url)?.let {
                    if (pattern.pattern.contains("BV")) {
                        return it.groupValues[1]
                    }
                    return null
                }
            }
            return null
        }
    }
}