package com.lovelycatv.vertex.ai.volc.tts.v1

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Document: https://www.volcengine.com/docs/6561/79823
 *
 * @author lovelycat
 * @since 2025-12-15 16:15
 * @version 1.0
 */
data class VolcanoTTSRequestV1(
    @SerializedName("app")
    val app: AppConfig,
    @SerializedName("user")
    val user: UserConfig,
    @SerializedName("audio")
    val audio: AudioConfig,
    @SerializedName("request")
    val request: RequestConfig
) {
    init {
        app.validate()
        user.validate()
        audio.validate()
        request.validate()
    }

    data class AppConfig(
        @SerializedName("appid")
        val appId: String,
        @SerializedName("token")
        val token: String,
        @SerializedName("cluster")
        val cluster: String
    ) {
        fun validate() {
            require(appId.isNotBlank()) { "appId must not be blank" }
            require(token.isNotBlank()) { "token must not be blank" }
            require(cluster.isNotBlank()) { "cluster must not be blank" }
        }
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
        @SerializedName("voice_type")
        val voiceType: String,
        // 8000 / 16000 / 24000
        @SerializedName("rate")
        val rate: Int? = 24000,
        // wav / pcm / ogg_opus / mp3
        @SerializedName("encoding")
        val encoding: Encoding = Encoding.PCM,
        // [1, 20]
        @SerializedName("compression_rate")
        val compressionRate: Int? = 1,
        // [0.2, 3]
        @SerializedName("speed_ratio")
        val speedRatio: Float? = 1.0f,
        // [0.1, 3]
        @SerializedName("volume_ratio")
        val volumeRatio: Float? = 1.0f,
        // [0.1, 3]
        @SerializedName("pitch_ratio")
        val pitchRatio: Float? = 1.0f,
        @SerializedName("emotion")
        val emotion: String? = null,
        @SerializedName("language")
        val language: String? = null
    ) {
        fun validate() {
            require(voiceType.isNotBlank()) { "voiceType must not be blank" }

            // rate: 8000 / 16000 / 24000
            if (rate != null) {
                require(rate == 8000 || rate == 16000 || rate == 24000) {
                    "rate must be one of 8000, 16000, 24000, but was $rate"
                }
            }

            // compressionRate: [1, 20]
            if (compressionRate != null) {
                require(compressionRate in 1..20) {
                    "compressionRate must be in [1, 20], but was $compressionRate"
                }
            }

            // speedRatio: [0.2, 3]
            if (speedRatio != null) {
                require(speedRatio in 0.2f..3.0f) {
                    "speedRatio must be in [0.2, 3.0], but was $speedRatio"
                }
            }

            // volumeRatio: [0.1, 3]
            if (volumeRatio != null) {
                require(volumeRatio in 0.1f..3.0f) {
                    "volumeRatio must be in [0.1, 3.0], but was $volumeRatio"
                }
            }

            // pitchRatio: [0.1, 3]
            if (pitchRatio != null) {
                require(pitchRatio in 0.1f..3.0f) {
                    "pitchRatio must be in [0.1, 3.0], but was $pitchRatio"
                }
            }
        }
    }

    data class RequestConfig(
        @SerializedName("reqid")
        val reqId: String = UUID.randomUUID().toString(),
        @SerializedName("text")
        val text: String,
        // plain / ssml
        @SerializedName("text_type")
        val textType: TextType = TextType.PLAIN,
        // query / submit
        @SerializedName("operation")
        val operation: Operation,
        @SerializedName("silence_duration")
        val silenceDuration: Int? = 125,
        @SerializedName("with_frontend")
        val withFrontend: String? = null,
        @SerializedName("frontend_type")
        val frontendType: String? = null,
        @SerializedName("with_timestamp")
        val withTimestamp: String? = null,
        @SerializedName("split_sentence")
        val splitSentence: String? = null,
        @SerializedName("pure_english_opt")
        val pureEnglishOpt: String? = null,
        @SerializedName("extra_param")
        val extraParam: String? = null
    ) {
        fun validate() {
            require(reqId.isNotBlank()) { "reqId must not be blank" }
            require(text.isNotBlank()) { "text must not be blank" }

            if (silenceDuration != null) {
                require(silenceDuration >= 0) {
                    "silenceDuration must be >= 0, but was $silenceDuration"
                }
            }
        }
    }

    enum class Encoding {
        @SerializedName("wav")
        WAV,
        @SerializedName("pcm")
        PCM,
        @SerializedName("ogg_opus")
        OGG_OPUS,
        @SerializedName("mp3")
        MP3
    }

    enum class TextType {
        @SerializedName("plain")
        PLAIN,
        @SerializedName("ssml")
        SSML
    }

    enum class Operation {
        @SerializedName("query")
        QUERY,
        @SerializedName("submit")
        SUBMIT
    }
}
