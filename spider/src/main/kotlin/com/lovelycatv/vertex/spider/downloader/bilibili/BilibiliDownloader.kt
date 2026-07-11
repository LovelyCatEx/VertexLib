package com.lovelycatv.vertex.spider.downloader.bilibili

import com.alibaba.fastjson2.JSONObject
import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpider
import com.lovelycatv.vertex.spider.adatper.selenium.ResponseInterceptor
import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpider
import com.lovelycatv.vertex.spider.downloader.DownloadConfig
import com.lovelycatv.vertex.spider.downloader.UrlFileDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BilibiliDownloader(
    private val jsoupSpider: JsoupSpider,
    private val seleniumSpider: SeleniumSpider<*, *>,
    private val tempDir: File = File(File("").canonicalPath, "tmp"),
) {
    private val logger = logger()

    companion object {
        private val URL_PATTERNS = listOf(
            Regex("""^https?://(?:www\.)?bilibili\.com/video/(BV[\w]+|av\d+)""", RegexOption.IGNORE_CASE)
        )
    }

    suspend fun parse(url: String, cookie: String? = null): BilibiliVideo {
        val scope = CoroutineScope(Dispatchers.IO)

        val videoId = this.extractVideoIdFromUrl(url)
            ?: throw IllegalStateException("Illegal bilibili video url: $url")

        val playerUrlRegex = Regex("""https?://[^/]+/x/player/(wbi/)?playurl(\?.*)?""")
        var playerUrlResult: BilibiliPlayerUrl? = null

        val countDownLatch = CountDownLatch(1)

        seleniumSpider.addInterceptor(ResponseInterceptor {
            logger.trace("Remote response captured: ${it.url}")
            if (playerUrlRegex.matches(it.url)) {
                val playerUrl = it.url

                logger.info("Player url captured: $playerUrl")

                scope.launch {
                    playerUrlResult = processPlayerUrl(playerUrl, cookie)
                    countDownLatch.countDown()
                }
            }
        })

        try {
            val doc = seleniumSpider.fetch(url)

            // Wait for all coroutines finished
            withContext(Dispatchers.IO) {
                countDownLatch.await(30, TimeUnit.SECONDS)
            }

            return BilibiliVideo(
                url = url,
                pageTitle = doc.title,
                videoId = videoId,
                playerUrl = playerUrlResult
            )
        } finally {
            seleniumSpider.quit()
        }
    }


    suspend fun downloadVideo(video: BilibiliVideo, outputFile: File): BilibiliVideoDownloadResult {
        val videoTmpDir = File(tempDir, video.videoId)

        return try {
            if (video.playerUrl == null) {
                throw IllegalArgumentException("Video player url is null")
            }

            val playerUrlResult = video.playerUrl

            logger.debug("Parsed url: {}", playerUrlResult.url)
            logger.debug("  Duration: {}", playerUrlResult.duration)
            logger.debug("  Supports qualities:")
            playerUrlResult.supportQualities.forEach {
                logger.debug("    Id: {}", it.id)
                logger.debug("    Description: {}", it.description)
                logger.debug("    Format: {}", it.format)
                logger.debug("    Codecs: {}", it.codecs)
                logger.debug("")
            }
            logger.debug("  Available Qualities:")
            playerUrlResult.qualities.forEach {
                logger.debug("    Id: {}", it.metadata.id)
                logger.debug("    Size: {}x{} ({}fps)", it.width, it.height, it.frameRate)
                logger.debug("    Urls: {}", it.urls.size)
                logger.debug("")
            }
            logger.debug("  Available Audios:")
            playerUrlResult.audios.forEach {
                logger.debug("    Id: {}", it.id)
                logger.debug("    Bandwidth: {}", it.bandwidth)
                logger.debug("    Urls: {}", it.urls.size)
                logger.debug("")
            }

            val highestQualityVideo = playerUrlResult.qualities.maxByOrNull { it.metadata.id }!!
            val audios = playerUrlResult.audios.sortedBy { it.bandwidth }.reversed()
            var audioIndex = 0
            var highestQualityAudio = audios[audioIndex]

            val downloader = UrlFileDownloader(
                config = DownloadConfig()
            )

            videoTmpDir.mkdirs()
            val videoTmpFile = File(videoTmpDir, "video.m4s")
            val audioTmpFile = File(videoTmpDir, "audio.m4s")

            val videoDownloadResult = downloader.downloadFromCandidateUrls(highestQualityVideo.urls, videoTmpFile)
            if (videoDownloadResult.isSuccess()) {
                logger.info("Video downloaded: {}", videoTmpFile.canonicalPath)
            }

            var audioDownloadResult = downloader.downloadFromCandidateUrls(highestQualityAudio.urls, audioTmpFile)
            while (!audioDownloadResult.isSuccess() && ++audioIndex < audios.size) {
                highestQualityAudio = audios[audioIndex]
                audioDownloadResult = downloader.downloadFromCandidateUrls(highestQualityAudio.urls, audioTmpFile)
            }
            logger.info("Audio downloaded: {}", audioTmpFile.canonicalPath)

            if (!videoDownloadResult.isSuccess() || !audioDownloadResult.isSuccess()) {
                throw IllegalStateException(
                    "Video or audio download failed, video=${videoDownloadResult.isSuccess()}, audio=${audioDownloadResult.isSuccess()}"
                )
            }

            val mergeResult = mergeVideoAudio(
                videoTmpFile.canonicalPath,
                audioTmpFile.canonicalPath,
                outputFile.canonicalPath
            )

            if (mergeResult) {
                logger.info("Video ${video.videoId} download successfully: {}", outputFile.canonicalPath)

                BilibiliVideoDownloadResult(
                    video = video,
                    outputFile = outputFile,
                    rawVideo = highestQualityVideo,
                    rawAudio = highestQualityAudio,
                )
            } else {
                throw IllegalStateException("Merge video with audio failed")
            }
        } catch (e: Exception) {
            logger.error("Video download failed", e)
            throw e
        } finally {
            // Clear tmp directory
            videoTmpDir.deleteRecursively()
        }
    }

    private suspend fun processPlayerUrl(url: String, cookie: String? = null): BilibiliPlayerUrl {
        val playerData = jsoupSpider.get(
            url,
            cookie?.let {
                mapOf("Cookie" to it)
            }
        )
            ?: throw IllegalStateException("Could not fetch data fro player url: $url")

        val data = JSONObject.parseObject(playerData).getJSONObject("data")
        val supportQualities = data.getJSONArray("support_formats").map {
            val item = it as JSONObject

            BilibiliPlayerUrl.QualityMetadata(
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

            BilibiliPlayerUrl.Quality(
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
        }.values.toList()

        val audios = dash.getJSONArray("audio").map {
            val item = it as JSONObject

            BilibiliPlayerUrl.Audio(
                id = item.getInteger("id"),
                bandwidth = item.getInteger("bandwidth"),
                urls = setOfNotNull(
                    item.getString("baseUrl"),
                    item.getString("base_url"),
                    *item.getJSONArray("backupUrl").map { it.toString() }.toTypedArray(),
                    *item.getJSONArray("backup_url").map { it.toString() }.toTypedArray(),
                ).toList()
            )
        }

        return BilibiliPlayerUrl(
            url = url,
            duration = duration,
            supportQualities = supportQualities.values.toList(),
            qualities = qualities,
            audios = audios,
        )
    }

    fun extractVideoIdFromUrl(url: String): String? {
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

    private fun mergeVideoAudio(videoPath: String, audioPath: String, outputPath: String): Boolean {
        return try {
            val command = arrayOf(
                "ffmpeg",
                "-i", videoPath,
                "-i", audioPath,
                "-c", "copy",
                "-y",
                outputPath
            )

            val process = ProcessBuilder(*command).start()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                true
            } else {
                val error = BufferedReader(InputStreamReader(process.errorStream))
                error.lines().forEach { logger.error(it) }
                false
            }
        } catch (e: Exception) {
            logger.error("Could not merge video with audio", e)
            false
        }
    }
}