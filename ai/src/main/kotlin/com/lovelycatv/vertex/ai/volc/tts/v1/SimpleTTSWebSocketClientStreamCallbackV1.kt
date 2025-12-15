package com.lovelycatv.vertex.ai.volc.tts.v1

/**
 * @author lovelycat
 * @since 2025-12-15 21:24
 * @version 1.0
 */
open class SimpleTTSWebSocketClientStreamCallbackV1 : TTSWebsocketClientV1.StreamCallback {
    override fun onSendSuccessful() {}

    override fun onSendFailed() {}

    override fun onReceived(data: ByteArray) {}

    override fun onCompleted() {}

    override fun onError(throwable: Throwable) {}
}