package com.lovelycatv.vertex.ai.volc.asr

/**
 * High level callback for a big model streaming ASR (SAUC) session.
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
interface VolcanoASRStreamCallback {
    /** The recognition session is ready to receive audio. */
    fun onReady()

    /**
     * A recognition result was received. During a session this is called multiple
     * times with the accumulated text; the final call has [isLast] set to true.
     */
    fun onResult(response: VolcanoASRResponse, isLast: Boolean)

    /** The session finished (the terminating package was received). */
    fun onCompleted(response: VolcanoASRResponse)

    /** Any error happened during the session. */
    fun onError(t: Throwable)
}
