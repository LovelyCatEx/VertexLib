package com.lovelycatv.vertex.ai.openai

import com.google.gson.Gson

/**
 * @author lovelycat
 * @since 2025-12-17 15:52
 * @version 1.0
 */
data class VertexAIClientConfig(
    val baseUrl: String,
    val apiKey: String,
    val timeoutSeconds: Long = 60,
    val enableLogging: Boolean = false
)