package com.lovelycatv.vertex.spider.downloader.bilibili

import java.io.File

data class BilibiliVideoDownloadResult(
    val video: BilibiliVideo,
    val outputFile: File,
    val rawVideo: BilibiliPlayerUrl.Quality,
    val rawAudio: BilibiliPlayerUrl.Audio,
)
