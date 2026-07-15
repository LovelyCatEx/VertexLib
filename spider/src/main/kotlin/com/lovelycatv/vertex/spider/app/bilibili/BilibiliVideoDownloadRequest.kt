package com.lovelycatv.vertex.spider.app.bilibili

import com.lovelycatv.vertex.spider.app.bilibili.types.BilibiliPlayerInfo

data class BilibiliVideoDownloadRequest(
    val playerInfo: BilibiliPlayerInfo,
    val selections: List<Selection>,
    val outputDir: String,
    val videoQualityId: Int? = null,
    val audioQualityId: Int? = null,
    val fileNameProducer: (Selection) -> String = DEFAULT_NAME_PRODUCER,
) {
    enum class Selection(val priority: Int) {
        VIDEO(2),
        VIDEO_ONLY(0),
        AUDIO_ONLY(1),
        VIDEO_TMP(0),
        AUDIO_TMP(1);
    }

    companion object {
        val DEFAULT_NAME_PRODUCER = { it: Selection ->
            when (it) {
                Selection.VIDEO -> "video.mp4"
                Selection.VIDEO_ONLY, Selection.VIDEO_TMP -> "raw_video.m4s"
                Selection.AUDIO_ONLY, Selection.AUDIO_TMP -> "audio.m4s"
            }
        }
    }
}
