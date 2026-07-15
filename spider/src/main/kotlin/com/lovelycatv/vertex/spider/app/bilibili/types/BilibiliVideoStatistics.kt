package com.lovelycatv.vertex.spider.app.bilibili.types

data class BilibiliVideoStatistics(
    val view: Int,
    val danmaku: Int,
    val reply: Int,
    val favorite: Int,
    val coin: Int,
    val like: Int,
    val share: Int,
    val dislike: Int,
)
