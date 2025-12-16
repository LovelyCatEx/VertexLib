package com.lovelycatv.vertex.ai.openai.request

/**
 * @author lovelycat
 * @since 2025-12-16 13:15
 * @version 1.0
 */
data class EmbeddingRequest(
    val model: String,
    val input: List<String>
) {
    constructor(model: String, input: String) : this(model, listOf(input))
}