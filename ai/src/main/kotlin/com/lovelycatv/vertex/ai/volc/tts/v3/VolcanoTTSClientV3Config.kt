package com.lovelycatv.vertex.ai.volc.tts.v3

import com.lovelycatv.vertex.ai.volc.VolcanoEngineConstants

/**
 * @author lovelycat
 * @since 2025-12-16 00:02
 * @version 1.0
 */
data class VolcanoTTSClientV3Config(
    val appId: String,
    val accessToken: String,
    val timeoutSeconds: Long = 60,
    val enableLogging: Boolean = false,
    val httpUrl: String = VolcanoEngineConstants.TTS_V3_HTTP_API_URL,
    val webSocketUrl: String = VolcanoEngineConstants.TTS_V3_WS_API_URL
)
