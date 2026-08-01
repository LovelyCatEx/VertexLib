package com.lovelycatv.vertex.ai.volc.asr

import com.google.gson.annotations.SerializedName

/**
 * Parsed JSON payload of a big model streaming ASR (SAUC) server response.
 *
 * Document: https://docs.volcengine.com/docs/6561/1354869
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
data class VolcanoASRResponse(
    @SerializedName("audio_info")
    val audioInfo: AudioInfo? = null,
    val result: Result? = null
) {
    data class AudioInfo(
        /**
         * Duration of audio received so far, in milliseconds.
         */
        val duration: Long = 0
    )

    data class Result(
        /**
         * Full recognized text accumulated so far.
         */
        val text: String = "",
        val utterances: List<Utterance>? = null
    )

    data class Utterance(
        val text: String = "",
        /**
         * Whether this utterance is finalized (will not change anymore).
         */
        val definite: Boolean = false,
        @SerializedName("start_time")
        val startTime: Long = 0,
        @SerializedName("end_time")
        val endTime: Long = 0,
        val words: List<Word>? = null
    ) {
        data class Word(
            val text: String = "",
            @SerializedName("start_time")
            val startTime: Long = 0,
            @SerializedName("end_time")
            val endTime: Long = 0
        )
    }
}
