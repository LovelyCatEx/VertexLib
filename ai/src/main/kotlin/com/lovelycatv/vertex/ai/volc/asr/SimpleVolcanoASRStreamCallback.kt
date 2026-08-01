package com.lovelycatv.vertex.ai.volc.asr

/**
 * No-op base implementation so callers only override what they need.
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
open class SimpleVolcanoASRStreamCallback : VolcanoASRStreamCallback {
    override fun onReady() {}

    override fun onResult(response: VolcanoASRResponse, isLast: Boolean) {}

    override fun onCompleted(response: VolcanoASRResponse) {}

    override fun onError(t: Throwable) {}
}