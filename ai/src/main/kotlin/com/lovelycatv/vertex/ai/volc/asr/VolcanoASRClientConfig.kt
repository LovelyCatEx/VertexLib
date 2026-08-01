package com.lovelycatv.vertex.ai.volc.asr

import com.lovelycatv.vertex.ai.volc.VolcanoEngineConstants

/**
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
data class VolcanoASRClientConfig(
    val appId: String,
    val accessToken: String,
    val resourceId: String = VolcanoEngineConstants.ASR_BIGMODEL_RESOURCE_ID,
    val timeoutSeconds: Long = 60,
    val enableLogging: Boolean = false,
    val webSocketUrl: String = VolcanoEngineConstants.ASR_BIGMODEL_WS_API_URL
)
