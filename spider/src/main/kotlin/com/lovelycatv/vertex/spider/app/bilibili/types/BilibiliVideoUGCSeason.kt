package com.lovelycatv.vertex.spider.app.bilibili.types

data class BilibiliVideoUGCSeason(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val mid: Long,
    val sections: List<Section>,
) {
    data class Section(
        val id: Long,
        val title: String,
        val episodes: List<Episode>
    ) {
        data class Episode(
            val bvId: String,
            val aid: Long,
            val cid: Long,
            val coverUrl: String,
            val title: String,
            val createdTime: Long,
            val publishedTime: Long,
            val width: Int,
            val height: Int,
            val duration: Int,
            val author: BilibiliVideoAuthor,
            val statistics: BilibiliVideoStatistics,
        )
    }
}
