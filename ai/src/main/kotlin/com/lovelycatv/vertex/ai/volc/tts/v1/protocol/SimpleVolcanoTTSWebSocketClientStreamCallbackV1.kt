package com.lovelycatv.vertex.ai.volc.tts.v1.protocol

/**
 * @author lovelycat
 * @since 2025-12-15 21:24
 * @version 1.0
 */
open class SimpleVolcanoTTSWebSocketClientStreamCallbackV1 : VolcanoTTSWebsocketClientV1.StreamCallbackV1 {
    override fun onSendSuccessful() {}

    override fun onSendFailed() {}

    override fun onReceived(data: ByteArray) {}

    override fun onCompleted() {}

    override fun onError(throwable: Throwable) {}
}