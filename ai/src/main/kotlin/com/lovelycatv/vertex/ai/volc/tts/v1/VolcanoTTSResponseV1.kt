package com.lovelycatv.vertex.ai.volc.tts.v1

import com.google.gson.annotations.SerializedName

/**
 * Document: https://www.volcengine.com/docs/6561/79823
 *
 * @author lovelycat
 * @since 2025-12-15 20:18
 * @version 1.0
 */
data class VolcanoTTSResponseV1(
    @SerializedName("reqid")
    val reqId: String,
    val code: Int,
    val message: String,
    val sequence: Int,
    val data: String,
    val addition: Addition
) {
    data class Addition(
        val duration: String,
        val frontend: String
    ) {
        val durationMillis: Long = duration.toLong()
    }
}
