package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

import com.lovelycatv.vertex.ai.volc.tts.v3.VolcanoTTSResponseV3

/**
 * @author lovelycat
 * @since 2025-12-16 01:33
 * @version 1.0
 */
open class SimpleVolcanoTTSWebSocketClientStreamCallbackV3 : VolcanoTTSWebSocketClientV3.StreamCallbackV3 {
    override fun onConnected() {}

    override fun onMessage(message: Message) {}

    override fun onSendSuccessfully(message: Message) {}

    override fun onSendFailed(message: Message) {}

    override fun onClosing(code: Int, reason: String) {}

    override fun onClosed(code: Int, reason: String) {}

    override fun onError(t: Throwable) {}

    override fun onCompleted(
        sentence: VolcanoTTSResponseV3.Sentence,
        usage: VolcanoTTSResponseV3.Usage
    ) {}

    override fun onReceived(payload: ByteArray) {}
}