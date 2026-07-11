package com.lovelycatv.vertex.spider.downloader.bilibili

data class BilibiliPlayerUrl(
    val url: String,
    val duration: Int,
    val supportQualities: List<QualityMetadata>,
    val qualities: List<Quality>,
    val audios: List<Audio>,
) {
    data class QualityMetadata(
        val id: Int,
        val format: String,
        val description: String,
        val codecs: List<String>,
    )

    data class Quality(
        val metadata: QualityMetadata,
        val width: Int,
        val height: Int,
        val frameRate: Int,
        val urls: List<String>,
    )

    data class Audio(
        val id: Int,
        val bandwidth: Int,
        val urls: List<String>,
    )
}
