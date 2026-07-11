package com.lovelycatv.vertex.spider.downloader.bilibili

data class BilibiliVideo(
    val url: String,
    val pageTitle: String,
    val videoId: String,
    val playerUrl: BilibiliPlayerUrl?
)
