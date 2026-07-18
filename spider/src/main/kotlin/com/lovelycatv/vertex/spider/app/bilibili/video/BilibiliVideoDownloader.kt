package com.lovelycatv.vertex.spider.app.bilibili.video

import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliPlayerInfo
import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliVideo
import com.lovelycatv.vertex.spider.downloader.DownloadResult
import com.lovelycatv.vertex.spider.downloader.UrlFileDownloader
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.PriorityQueue

class BilibiliVideoDownloader(
    private val urlFileDownloader: UrlFileDownloader,
    private val tempDir: String,
    maxParallelDownloads: Int = 5,
) {
    private val logger = logger()

    private val semaphore = Semaphore(maxParallelDownloads)

    private val coroutineScope = CoroutineScope(
        Dispatchers.IO + CoroutineName("BilibiliVideoDownloader")
    )

    fun download(request: BilibiliVideoDownloadRequest): Deferred<BilibiliVideoDownloadResult> {
        logger.info(
            "Preparing to download bilibili video, bvId={}, cid={}, selections={}",
            request.playerInfo.bvId,
            request.playerInfo.cid,
            request.selections.joinToString(", ")
        )

        // Check parameters
        val selectedVideo = request.videoQualityId?.let {
            require(
                request.playerInfo.videos.contains(it)
            ) {
                "Specified video quality ${request.videoQualityId} doesn't exist in ${
                    request.playerInfo.videos.keys.joinToString(
                        ", "
                    )
                }"
            }

            request.playerInfo.videos[it]
        } ?: request.playerInfo.highestQualityVideo

        val selectedAudio = request.audioQualityId?.let {
            require(
                request.playerInfo.audios.contains(it)
            ) {
                "Specified audio quality ${request.audioQualityId} doesn't exist in ${
                    request.playerInfo.audios.keys.joinToString(
                        ", "
                    )
                }"
            }

            request.playerInfo.audios[it]
        } ?: request.playerInfo.highestQualityAudio

        // Prepare output directory
        val outputDir = File(request.outputDir)
        if (outputDir.exists() && !outputDir.isDirectory) {
            throw IllegalStateException("Output dir must be a directory")
        }

        outputDir.mkdirs()

        // Prepare temp directory
        val tempDir = File(tempDir, request.playerInfo.bvId)
        tempDir.mkdirs()
        logger.info("Temp directory prepared: ${tempDir.canonicalPath}")

        // Prepared sorted selections
        val selectionQueue = PriorityQueue<BilibiliVideoDownloadRequest.Selection> { a, b ->
            a.priority - b.priority
        }

        request.selections.toSet().forEach { selection ->
            selectionQueue.add(selection)
        }

        val selectionResults = mutableMapOf<BilibiliVideoDownloadRequest.Selection, DownloadResult>()
        var videoPrepared = false
        var audioPrepared = false

        // Temp files
        val rawVideoTmpFile = File(
            tempDir,
            request.fileNameProducer.invoke(BilibiliVideoDownloadRequest.Selection.VIDEO_TMP)
        )
        val rawAudioTmpFile = File(
            tempDir,
            request.fileNameProducer.invoke(BilibiliVideoDownloadRequest.Selection.AUDIO_TMP)
        )

        return coroutineScope.async(CoroutineName("B-${request.playerInfo.bvId}")) {
            semaphore.withPermit {
                // Process selections
                while (selectionQueue.isNotEmpty()) {
                    when (val selection = selectionQueue.poll()) {
                        BilibiliVideoDownloadRequest.Selection.VIDEO -> {
                            // Depends on VIDEO_ONLY and AUDIO_ONLY

                            if (!videoPrepared || !audioPrepared) {
                                if (!videoPrepared) {
                                    selectionQueue.add(BilibiliVideoDownloadRequest.Selection.VIDEO_TMP)
                                }
                                if (!audioPrepared) {
                                    selectionQueue.add(BilibiliVideoDownloadRequest.Selection.AUDIO_TMP)
                                }

                                selectionQueue.add(BilibiliVideoDownloadRequest.Selection.VIDEO)

                                continue
                            }

                            if (!rawVideoTmpFile.exists() || !rawAudioTmpFile.exists()) {
                                throw IllegalStateException(
                                    "Video and audio tmp file marked prepared but does not exist, " +
                                            "rawVideo=${rawVideoTmpFile.exists()}, rawAudio=${rawAudioTmpFile.exists()}"
                                )
                            }

                            val outputFile = File(
                                outputDir,
                                request.fileNameProducer.invoke(selection)
                            )

                            val mergeResult = mergeVideoAudio(
                                rawVideoTmpFile.canonicalPath,
                                rawAudioTmpFile.canonicalPath,
                                outputFile.canonicalPath
                            )

                            if (mergeResult) {
                                logger.info(
                                    "Video {} download successfully. OutputFile={}",
                                    request.playerInfo.bvId,
                                    outputFile.canonicalPath
                                )

                                selectionResults[selection] = DownloadResult.Success(outputFile, outputFile.length())

                                // Clear temp files
                                tempDir.deleteRecursively()
                            } else {
                                logger.warn(
                                    "Video {} merge failed, keeping temp files.",
                                    request.playerInfo.bvId,
                                )

                                selectionResults[selection] = DownloadResult.Failure(-1, "Video merge failed")
                            }
                        }

                        BilibiliVideoDownloadRequest.Selection.VIDEO_ONLY,
                        BilibiliVideoDownloadRequest.Selection.VIDEO_TMP -> {
                            logger.info(
                                "Downloading raw video, qualityId={}, format={}, codecs={}",
                                selectedVideo.metadata.id,
                                selectedVideo.metadata.format + "/" + selectedVideo.metadata.description,
                                selectedVideo.metadata.codecs?.joinToString(", ")
                            )

                            val result = if (!rawVideoTmpFile.exists()) {
                                urlFileDownloader.downloadFromCandidateUrls(selectedVideo.urls, rawVideoTmpFile)
                            } else {
                                DownloadResult.Success(rawVideoTmpFile, rawVideoTmpFile.length())
                            }

                            selectionResults[selection] = result

                            if (result.isSuccess()) {
                                logger.info(
                                    "Raw video downloaded, bvId={}, cid={}",
                                    request.playerInfo.bvId,
                                    request.playerInfo.cid
                                )

                                videoPrepared = true

                                if (selection == BilibiliVideoDownloadRequest.Selection.VIDEO_ONLY) {
                                    val outputVideoFile = File(outputDir, request.fileNameProducer.invoke(selection))
                                    (result as DownloadResult.Success).file.copyTo(outputVideoFile, true)
                                }
                            } else {
                                logger.warn(
                                    "Download raw video failed, bvId={}, cid={}",
                                    request.playerInfo.bvId,
                                    request.playerInfo.cid
                                )
                                audioPrepared = false
                            }
                        }

                        BilibiliVideoDownloadRequest.Selection.AUDIO_ONLY,
                        BilibiliVideoDownloadRequest.Selection.AUDIO_TMP -> {
                            logger.info(
                                "Downloading raw audio, qualityId={}, bandwidth={}",
                                selectedAudio.id,
                                selectedAudio.bandwidth
                            )

                            val result = if (!rawAudioTmpFile.exists()) {
                                urlFileDownloader.downloadFromCandidateUrls(selectedAudio.urls, rawAudioTmpFile)
                            } else {
                                DownloadResult.Success(rawAudioTmpFile, rawAudioTmpFile.length())
                            }

                            selectionResults[selection] = result

                            if (result.isSuccess()) {
                                logger.info(
                                    "Raw audio downloaded, bvId={}, cid={}",
                                    request.playerInfo.bvId,
                                    request.playerInfo.cid
                                )

                                audioPrepared = true

                                if (selection == BilibiliVideoDownloadRequest.Selection.AUDIO_ONLY) {
                                    val outputAudioFile = File(outputDir, request.fileNameProducer.invoke(selection))
                                    (result as DownloadResult.Success).file.copyTo(outputAudioFile, true)
                                }
                            } else {
                                logger.warn(
                                    "Download raw audio failed, bvId={}, cid={}",
                                    request.playerInfo.bvId,
                                    request.playerInfo.cid
                                )

                                audioPrepared = false
                            }
                        }
                    }
                }

                // Return
                BilibiliVideoDownloadResult(
                    request = request,
                    details = selectionResults,
                )
            }
        }
    }

    fun generateOutputFileName(
        bilibiliVideo: BilibiliVideo,
        video: BilibiliPlayerInfo.Video,
        audio: BilibiliPlayerInfo.Audio,
    ): String {
        return "%s_v-%s_%s_%s_%s_a-%skbps_%s".format(
            normalizeVideoTitle(bilibiliVideo.title),
            video.metadata.description,
            "${video.width}x${video.height}(${video.frameRate}fps)",
            video.metadata.format,
            video.metadata.codecs?.joinToString(", ") ?: "unknownCodec",
            audio.bandwidth,
            audio.codecs,
        )
    }

    private fun normalizeVideoTitle(title: String): String {
        return title
            .replace("/", "_")
            .replace("\\", "_")
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