package com.lovelycatv.vertex.spider.app.bilibili.types

data class BilibiliVideo(
    val bvId: String,
    val aid: Long,
    val cid: Long,
    val coverUrl: String,
    val title: String,
    val description: String,
    val createdTime: Long,
    val publishedTime: Long,
    val width: Int,
    val height: Int,
    val duration: Int,
    val author: BilibiliVideoAuthor,
    val statistics: BilibiliVideoStatistics,
    val ugcSeason: BilibiliVideoUGCSeason?,
)
