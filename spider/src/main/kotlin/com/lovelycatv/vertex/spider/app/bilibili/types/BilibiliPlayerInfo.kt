package com.lovelycatv.vertex.spider.app.bilibili.types

data class BilibiliPlayerInfo(
    val bvId: String,
    val cid: Long,
    val url: String,
    val duration: Int,
    val supportQualities: List<QualityMetadata>,
    val videos: Map<Int, Video>,
    val audios: Map<Int, Audio>,
) {
    val highestQualityVideo get() = this.videos[this.videos.keys.maxOf { it }]!!
    val highestQualityAudio get() = this.audios[this.audios.keys.maxOf { it }]!!

    data class QualityMetadata(
        val id: Int,
        val format: String,
        val description: String,
        val codecs: List<String>?,
    )

    data class Video(
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
        val codecs: String?,
    )

    fun prettyInspection(): String {
        val lines = mutableListOf<String>()

        lines.add("url: $url,")
        lines.add("bvId: $bvId, cid: $cid")
        lines.add("duration: $duration seconds")

        if (supportQualities.isNotEmpty()) {
            lines.add("supportQualities:")
            supportQualities.forEach {
                lines.add("  - ${it.description}: id=${it.id}, format=${it.format}, codecs=${it.codecs}")
            }
        } else {
            lines.add("supportQualities: []")
        }

        if (videos.values.isNotEmpty()) {
            lines.add("videos:")
            videos.values.forEach {
                lines.add("  - ${it.metadata.description}: id=${it.metadata.id}, scale=${it.width}x${it.height}(${it.frameRate}fps), urls=${it.urls.size}")
            }
        } else {
            lines.add("videos: []")
        }

        if (audios.values.isNotEmpty()) {
            lines.add("audios:")
            audios.values.forEach {
                lines.add("  - ${it.bandwidth}kbps: id=${it.id}, urls=${it.urls.size}, codecs=${it.codecs}")
            }
        } else {
            lines.add("audios: []")
        }

        return lines.joinToString("\n")
    }
}
