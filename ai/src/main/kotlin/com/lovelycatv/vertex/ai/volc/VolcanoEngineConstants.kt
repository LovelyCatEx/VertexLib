package com.lovelycatv.vertex.ai.volc

/**
 * @author lovelycat
 * @since 2025-12-15 15:56
 * @version 1.0
 */
object VolcanoEngineConstants {
    // Document: https://www.volcengine.com/docs/6561/79820
    const val TTS_V1_HTTP_API_URL = "https://openspeech.bytedance.com/api/v1/tts"
    // Document: https://www.volcengine.com/docs/6561/79821
    const val TTS_V1_WS_API_URL = "wss://openspeech.bytedance.com/api/v1/tts/ws_binary"
    // Document: https://www.volcengine.com/docs/6561/1598757
    const val TTS_V3_HTTP_API_URL = "https://openspeech.bytedance.com/api/v3/tts/unidirectional"
    // Document: https://www.volcengine.com/docs/6561/1329505
    const val TTS_V3_WS_API_URL = "wss://openspeech.bytedance.com/api/v3/tts/bidirection"

    // Big model streaming ASR (SAUC)
    // Document: https://docs.volcengine.com/docs/6561/1354869
    const val ASR_BIGMODEL_WS_API_URL = "wss://openspeech.bytedance.com/api/v3/sauc/bigmodel_async"

    const val ASR_BIGMODEL_RESOURCE_ID = "volc.bigasr.sauc.duration"
    const val ASR_BIGMODEL_V2_RESOURCE_ID = "volc.seedasr.sauc.duration"
}