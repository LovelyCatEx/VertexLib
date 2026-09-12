package com.lovelycatv.vertex.ai.llm.config

data class LLMClientConfig(
    val baseUrl: String,
    val apiKey: String,
    val connectionTimeoutSeconds: Long = 30,
    val readTimeoutSeconds: Long = 60,
    val chatCompletionPath: String = "chat/completions",
    val embeddingPath: String = "embeddings",
    val llmResponseConfig: LLMResponseConfig = LLMResponseConfigDefaults.OPENAI,
) {
    val normalizedBaseUrl = baseUrl.run {
        if (baseUrl.endsWith("/")) this else "$baseUrl/"
    }

    fun normalizedPath(path: String): String {
        return if (path.startsWith("/"))
            path.drop(1)
        else
            path
    }
}