package com.lovelycatv.vertex.spider.app.bilibili

import com.lovelycatv.vertex.spider.downloader.DownloadResult

data class BilibiliVideoDownloadResult(
    val request: BilibiliVideoDownloadRequest,
    val details: Map<BilibiliVideoDownloadRequest.Selection, DownloadResult>,
) {
    val allSuccess: Boolean = details.all { it.value.isSuccess() }
}