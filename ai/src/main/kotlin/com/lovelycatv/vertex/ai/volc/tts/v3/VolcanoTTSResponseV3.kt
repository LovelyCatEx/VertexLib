package com.lovelycatv.vertex.ai.volc.tts.v3

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-15 23:26
 * @version 1.0
 */
data class VolcanoTTSResponseV3(
    val code: Int,
    val message: String,
    val data: String?,
    val sentence: Sentence?,
    val usage: Usage?
) {
    data class Sentence(
        val text: String,
        val words: List<Word>
    ) {
        data class Word(
            val confidence: Float,
            val startTime: Float,
            val endTime: Float,
            val word: String
        )
    }

    data class Usage(
        @SerializedName("text_words")
        val textWords: Int
    )
}