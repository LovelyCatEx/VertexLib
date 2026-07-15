package com.lovelycatv.vertex.spider.app.bilibili

import com.lovelycatv.vertex.spider.RequestOptions
import com.lovelycatv.vertex.spider.UserAgent
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpider
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpiderOptions
import com.lovelycatv.vertex.spider.downloader.DownloadConfig
import com.lovelycatv.vertex.spider.downloader.UrlFileDownloader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File

class BilibiliVideoDownloaderTest {
    @Test
    fun download() {
        val userAgent = UserAgent.chromeOnWindows().toString()
        val cookie = ""
        val headers = mapOf("Cookie" to listOf(cookie))
        val referer = "https://www.bilibili.com"
        val requestOptions = RequestOptions(
            headers = headers,
            referer = referer
        )

        val jsoupSpider = JsoupSpider(
            spiderOptions = JsoupSpiderOptions(
                userAgent = userAgent,
                requestOptions = requestOptions
            )
        )

        val parser = BilibiliVideoParser(
            jsoupSpider = jsoupSpider,
        )

        val downloader = BilibiliVideoDownloader(
            urlFileDownloader = UrlFileDownloader(
                config = DownloadConfig(
                    userAgent = userAgent,
                    headers = requestOptions.plainHeaders(),
                    referer = referer
                )
            ),
            tempDir = File(File("").canonicalPath, "downloads/tmp").canonicalPath,
            maxParallelDownloads = 1
        )

        val url = "https://www.bilibili.com/video/BV1DF411e7rc"
        runBlocking {
            val video = parser.parseVideo(url)
            val playerInfo = parser.getPlayerInfo(video)
            println(playerInfo.prettyInspection())
            val highestQualityVideo = playerInfo.highestQualityVideo
            val highestQualityAudio = playerInfo.highestQualityAudio
            val d1 = downloader.download(
                BilibiliVideoDownloadRequest(
                    playerInfo = playerInfo,

                    selections = listOf(
                        BilibiliVideoDownloadRequest.Selection.VIDEO,
                        BilibiliVideoDownloadRequest.Selection.VIDEO_ONLY,
                        BilibiliVideoDownloadRequest.Selection.AUDIO_ONLY,
                    ),
                    outputDir = File(File("").canonicalPath, "downloads/${normalizeVideoTitle(video.title)}").canonicalPath,
                    highestQualityVideo.metadata.id,
                    highestQualityAudio.id,
                ) {
                    when (it) {
                        BilibiliVideoDownloadRequest.Selection.VIDEO -> "${downloader.generateOutputFileName(video, highestQualityVideo, highestQualityAudio)}.mp4"
                        BilibiliVideoDownloadRequest.Selection.VIDEO_ONLY -> "${normalizeVideoTitle(video.title)}_video.m4s"
                        BilibiliVideoDownloadRequest.Selection.AUDIO_ONLY -> "${normalizeVideoTitle(video.title)}_audio.m4s"

                        else -> {
                            BilibiliVideoDownloadRequest.DEFAULT_NAME_PRODUCER.invoke(it)
                        }
                    }
                }
            )

            val d2 = downloader.download(
                BilibiliVideoDownloadRequest(
                    playerInfo = playerInfo,

                    selections = listOf(
                        BilibiliVideoDownloadRequest.Selection.VIDEO,
                        BilibiliVideoDownloadRequest.Selection.VIDEO_ONLY,
                        BilibiliVideoDownloadRequest.Selection.AUDIO_ONLY,
                    ),
                    outputDir = File(File("").canonicalPath, "downloads/${normalizeVideoTitle(video.title)}").canonicalPath,
                    highestQualityVideo.metadata.id,
                    highestQualityAudio.id,
                ) {
                    when (it) {
                        BilibiliVideoDownloadRequest.Selection.VIDEO -> "${downloader.generateOutputFileName(video, highestQualityVideo, highestQualityAudio)}.mp4"
                        BilibiliVideoDownloadRequest.Selection.VIDEO_ONLY -> "${normalizeVideoTitle(video.title)}_video.m4s"
                        BilibiliVideoDownloadRequest.Selection.AUDIO_ONLY -> "${normalizeVideoTitle(video.title)}_audio.m4s"

                        else -> {
                            BilibiliVideoDownloadRequest.DEFAULT_NAME_PRODUCER.invoke(it)
                        }
                    }
                }
            )

            d1.await()
            d2.await()
        }
    }

    fun normalizeVideoTitle(title: String): String {
        return title
            .replace("/", "_")
            .replace("\\", "_")
    }

}