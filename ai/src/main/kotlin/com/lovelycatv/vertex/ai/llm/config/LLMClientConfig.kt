package com.lovelycatv.vertex.ai.llm.config

data class LLMClientConfig(
    val baseUrl: String,
    val apiKey: String,
    val connectionTimeoutSeconds: Long = 30,
    val readTimeoutSeconds: Long = 60,
    val chatCompletionPath: String = "chat/completions",
    val embeddingPath: String = "embeddings",
    /** Both `/v1/models` shapes live here, so the default suits either provider. */
    val modelsPath: String = "models",
    val llmResponseConfig: LLMResponseConfig = LLMResponseConfigDefaults.OPENAI,
    /**
     * Extra request headers. They are applied after the client's own, so one naming the same
     * header replaces it — including the auth header, which is how a gateway expecting a
     * different scheme gets what it wants.
     */
    val headers: Map<String, String> = emptyMap(),
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