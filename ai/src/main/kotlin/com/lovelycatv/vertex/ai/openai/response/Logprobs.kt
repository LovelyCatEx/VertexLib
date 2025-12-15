package com.lovelycatv.vertex.ai.openai.response

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-13 02:44
 * @version 1.0
 */
data class Logprobs(
    val content: List<TopLogprobContent>
) {
    sealed class BaseContent(
        val token: String,
        val logprob: Float,
        val bytes: IntArray,
    )

    class TopLogprobContent(
        token: String,
        logprob: Float,
        bytes: IntArray,
        @SerializedName("top_logprobs")
        val topLogprobs: List<BaseContent>
    ) : BaseContent(token, logprob, bytes)
}