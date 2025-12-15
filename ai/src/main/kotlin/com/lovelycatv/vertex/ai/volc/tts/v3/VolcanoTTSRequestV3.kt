package com.lovelycatv.vertex.ai.volc.tts.v3

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Document: https://www.volcengine.com/docs/6561/1598757
 *
 * @author lovelycat
 * @since 2025-12-15 23:26
 * @version 1.0
 */
data class VolcanoTTSRequestV3(
    @SerializedName("user")
    val user: UserConfig,
    val namespace: String = "BidirectionalTTS",
    @SerializedName("req_params")
    val requestConfig: RequestConfig
) {

    init {
        validate()
    }

    fun validate() {
        require(namespace.isNotBlank()) { "namespace must not be blank" }
        user.validate()
        requestConfig.validate()
    }

    data class UserConfig(
        @SerializedName("uid")
        val uid: String
    ) {
        fun validate() {
            require(uid.isNotBlank()) { "uid must not be blank" }
        }
    }

    data class RequestConfig(
        /**
         * Model version. Example: "seed-tts-1.1".
         * If not provided, the default model will be used.
         */
        val model: String? = null,
        /**
         * SSML input text. Used when text is in SSML format.
         * Either 'text' or 'ssml' must be non-empty.
         */
        val ssml: String? = null,
        /**
         * Plain text input.
         * Either 'text' or 'ssml' must be non-empty.
         */
        val text: String? = null,
        /**
         * Required. Speaker ID. See the official speaker list.
         */
        val speaker: String,
        @SerializedName("audio_params")
        val audioParams: AudioConfig = AudioConfig(),
        @SerializedName("additions")
        val additions: String = AdditionConfig().toJSONString()
    ) {
        fun getValidRequestTest(): String {
            return this.ssml ?: this.text
            ?: throw IllegalArgumentException("Either text or ssml must be provided")
        }

        fun validate() {
            require(speaker.isNotBlank()) { "speaker must not be blank" }

            val hasText = !text.isNullOrBlank()
            val hasSsml = !ssml.isNullOrBlank()

            require(hasText || hasSsml) {
                "Either text or ssml must be provided"
            }

            audioParams.validate()
        }

        data class AudioConfig(
            /**
             * Audio encoding format: mp3 / ogg_opus / pcm.
             * WAV is accepted but not recommended for streaming (header repeats).
             */
            val format: AudioFormat = AudioFormat.MP3,
            /**
             * Sample rate. Allowed: [8000,16000,22050,24000,32000,44100,48000]
             * Default: 24000
             */
            @SerializedName("sample_rate")
            val sampleRate: Int? = 24000,
            /**
             * Bit rate for MP3. Server defaults to [64k,160k].
             */
            @SerializedName("bit_rate")
            val bitRate: Int? = null,
            /**
             * Emotion tag, e.g., "angry". Only supported by some speakers.
             */
            val emotion: String? = null,
            /**
             * Emotion intensity [1–5]. Default: 4.
             */
            @SerializedName("emotion_scale")
            val emotionScale: Int = 4,
            /**
             * Speech speed [-50,100].
             * 100 = 2x speed, -50 = 0.5x speed.
             */
            @SerializedName("speech_rate")
            val speechRate: Int = 0,
            /**
             * Loudness adjustment [-50,100].
             * 100 = 2x loudness, -50 = 0.5x.
             */
            @SerializedName("loudness_rate")
            val loudnessRate: Int = 0,
            /**
             * Only supported by TTS 1.0: return word-level timestamps.
             */
            @SerializedName("enable_timestamp")
            val enableTimestamp: Boolean = false
        ) {
            fun validate() {
                // Validate sample rate
                if (sampleRate != null) {
                    val allowed = setOf(8000, 16000, 22050, 24000, 32000, 44100, 48000)
                    require(sampleRate in allowed) {
                        "sampleRate must be one of $allowed, but was $sampleRate"
                    }
                }

                require(emotionScale in 1..5) {
                    "emotionScale must be in [1,5], but was $emotionScale"
                }

                require(speechRate in -50..100) {
                    "speechRate must be in [-50,100], but was $speechRate"
                }

                require(loudnessRate in -50..100) {
                    "loudnessRate must be in [-50,100], but was $loudnessRate"
                }

                if (bitRate != null) {
                    require(bitRate > 0) { "bitRate must be > 0, but was $bitRate" }
                }
            }
        }

        data class AdditionConfig(
            /**
             * Silence duration added at the end of speech, range [0,30000] ms.
             */
            @SerializedName("silence_duration")
            val silenceDuration: Int? = null,
            /**
             * Enable automatic language detection.
             */
            @SerializedName("enable_language_detector")
            val enableLanguageDetector: Boolean? = null,
            /**
             * Disable markdown filter. If true, markdown syntax is stripped.
             */
            @SerializedName("disable_markdown_filter")
            val disableMarkdownFilter: Boolean? = null,
            /**
             * Disable emoji filtering (emoji will remain in text).
             */
            @SerializedName("disable_emoji_filter")
            val disableEmojiFilter: Boolean? = null,
            /**
             * Parentheses filtering intensity [0–100].
             * 0 = no filtering, 100 = fully remove parentheses content.
             */
            @SerializedName("max_length_to_filter_parenthesis")
            val maxLengthToFilterParenthesis: Int? = 100,
            /**
             * Explicit language setting. See enum ExplicitLanguage.
             */
            @SerializedName("explicit_language")
            val explicitLanguage: ExplicitLanguage? = null,
            /**
             * Context language hint for the model.
             */
            @SerializedName("context_language")
            val contextLanguage: ContextLanguage? = null,
            /**
             * Threshold for ratio of unsupported characters, in (0.0, 1.0].
             */
            @SerializedName("unsupported_char_ratio_thresh")
            val unsupportedCharRatioThresh: Float? = 0.3f,
            /**
             * Whether to add rhythmic watermark at the end.
             */
            @SerializedName("aigc_watermark")
            val aigcWatermark: Boolean? = null,
            /**
             * AIGC metadata inserted into audio header (mp3/wav/ogg_opus).
             */
            @SerializedName("aigc_metadata")
            val aigcMetadata: AigcMetadata? = null,
            /**
             * Cache settings (1 hour retention).
             */
            @SerializedName("cache_config")
            val cacheConfig: CacheConfig? = null,
            /**
             * Post-processing configuration, e.g., pitch adjustment.
             */
            @SerializedName("post_process")
            val postProcess: PostProcess? = null,
            /**
             * Contextual hint text (only first element is used).
             * Only supported in TTS 2.0.
             */
            @SerializedName("context_texts")
            val contextTexts: List<String>? = null,
            /**
             * Mix-speaker configuration. Only supported in TTS 1.0.
             */
            @SerializedName("mix_speaker")
            val mixSpeaker: MixSpeaker? = null
        ) {
            init {
                validate()
            }

            fun toJSONString(gson: Gson = Gson()): String {
                return gson.toJson(this)
            }

            fun validate() {
                if (silenceDuration != null) {
                    require(silenceDuration in 0..30000) {
                        "silenceDuration must be in [0,30000], but was $silenceDuration"
                    }
                }

                if (maxLengthToFilterParenthesis != null) {
                    require(maxLengthToFilterParenthesis in 0..100) {
                        "maxLengthToFilterParenthesis must be in [0,100], but was $maxLengthToFilterParenthesis"
                    }
                }

                if (unsupportedCharRatioThresh != null) {
                    require(unsupportedCharRatioThresh in 0.0f..1.0f) {
                        "unsupportedCharRatioThresh must be in [0.0,1.0], but was $unsupportedCharRatioThresh"
                    }
                }

                cacheConfig?.validate()
                postProcess?.validate()
                mixSpeaker?.validate()
            }

            data class AigcMetadata(
                @SerializedName("enable")
                val enable: Boolean = false,
                @SerializedName("content_producer")
                val contentProducer: String? = null,
                @SerializedName("produce_id")
                val produceId: String? = null,
                @SerializedName("content_propagator")
                val contentPropagator: String? = null,
                @SerializedName("propagate_id")
                val propagateId: String? = null
            )

            data class CacheConfig(
                @SerializedName("text_type")
                val textType: Int? = null,
                @SerializedName("use_cache")
                val useCache: Boolean? = null
            ) {
                fun validate() {
                    if (textType != null) {
                        require(textType == 1) {
                            "cache_config.text_type must be 1 when provided, but was $textType"
                        }
                    }
                }
            }

            data class PostProcess(
                /**
                 * Pitch adjustment [-12,12]
                 */
                val pitch: Int? = null
            ) {
                fun validate() {
                    if (pitch != null) {
                        require(pitch in -12..12) {
                            "post_process.pitch must be in [-12,12], but was $pitch"
                        }
                    }
                }
            }

            data class MixSpeaker(
                val speakers: List<MixSpeakerItem>
            ) {
                fun validate() {
                    require(speakers.isNotEmpty()) { "mix_speaker.speakers must not be empty" }
                    require(speakers.size <= 3) { "mix_speaker.speakers must contain ≤3 items" }

                    val totalFactor = speakers.sumOf { it.mixFactor.toDouble() }
                    require(totalFactor in 0.99..1.01) {
                        "Sum of mix_factor must be approximately 1.0, but was $totalFactor"
                    }

                    speakers.forEach { it.validate() }
                }

                data class MixSpeakerItem(
                    @SerializedName("source_speaker")
                    val sourceSpeaker: String,
                    @SerializedName("mix_factor")
                    val mixFactor: Float
                ) {
                    fun validate() {
                        require(sourceSpeaker.isNotBlank()) {
                            "mix_speaker.speakers.source_speaker must not be blank"
                        }
                        require(mixFactor > 0f) {
                            "mix_factor must be > 0, but was $mixFactor"
                        }
                    }
                }
            }
        }
    }

    enum class AudioFormat {
        @SerializedName("mp3")
        MP3,
        @SerializedName("ogg_opus")
        OGG_OPUS,
        @SerializedName("pcm")
        PCM,
        @SerializedName("wav")
        WAV
    }

    enum class ExplicitLanguage {
        @SerializedName("crosslingual")
        CROSSLINGUAL,
        @SerializedName("zh-cn")
        ZH_CN,
        @SerializedName("zh")
        ZH,
        @SerializedName("en")
        EN,
        @SerializedName("ja")
        JA,
        @SerializedName("es-mx")
        ES_MX,
        @SerializedName("id")
        ID,
        @SerializedName("pt-br")
        PT_BR,
        @SerializedName("de")
        DE,
        @SerializedName("fr")
        FR
    }

    enum class ContextLanguage {
        @SerializedName("id")
        ID,
        @SerializedName("es")
        ES,
        @SerializedName("pt")
        PT
    }
}
