package com.lovelycatv.vertex.ai.volc.asr

import com.google.gson.annotations.SerializedName

/**
 * Full client request payload that opens a big model streaming ASR (SAUC) session.
 *
 * Document: https://docs.volcengine.com/docs/6561/1354869
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
data class VolcanoASRRequest(
    @SerializedName("user")
    val user: UserConfig,
    @SerializedName("audio")
    val audio: AudioConfig = AudioConfig(),
    @SerializedName("request")
    val request: RequestConfig = RequestConfig()
) {

    init {
        validate()
    }

    fun validate() {
        user.validate()
        audio.validate()
    }

    data class UserConfig(
        @SerializedName("uid")
        val uid: String
    ) {
        fun validate() {
            require(uid.isNotBlank()) { "uid must not be blank" }
        }
    }

    data class AudioConfig(
        /**
         * Container format: "wav", "pcm", "ogg". Live microphone input should use "pcm".
         */
        val format: String = "pcm",
        /**
         * Codec: "raw" (pcm), "opus". Default "raw".
         */
        val codec: String = "raw",
        /**
         * Sample rate in Hz. Must match the captured audio, default 16000.
         */
        val rate: Int = 16000,
        /**
         * Bits per sample, default 16.
         */
        val bits: Int = 16,
        /**
         * Number of channels, default 1 (mono).
         */
        val channel: Int = 1
    ) {
        fun validate() {
            require(format.isNotBlank()) { "audio.format must not be blank" }
            require(codec.isNotBlank()) { "audio.codec must not be blank" }
            require(rate > 0) { "audio.rate must be > 0, but was $rate" }
            require(bits > 0) { "audio.bits must be > 0, but was $bits" }
            require(channel > 0) { "audio.channel must be > 0, but was $channel" }
        }
    }

    data class RequestConfig(
        @SerializedName("model_name")
        val modelName: String = "bigmodel",
        /**
         * Enable inverse text normalization (e.g. "一百" -> "100").
         */
        @SerializedName("enable_itn")
        val enableItn: Boolean = true,
        /**
         * Enable punctuation.
         */
        @SerializedName("enable_punc")
        val enablePunc: Boolean = true,
        /**
         * Enable disfluency detection & correction.
         */
        @SerializedName("enable_ddc")
        val enableDdc: Boolean = true,
        /**
         * Whether to return word/utterance level details.
         */
        @SerializedName("show_utterances")
        val showUtterances: Boolean = true,
        /**
         * When true the whole audio is recognized as a single non-streaming request.
         */
        @SerializedName("enable_nonstream")
        val enableNonstream: Boolean = false
    )
}
