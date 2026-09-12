package com.lovelycatv.vertex.ai.llm

data class LLMUsageResolverJsonPathConfig(
    val promptTokensPath: String? = null,
    val completionTokensPath: String? = null,
    val reasoningTokensPath: String? = null,
    val cacheReadTokensPath: String? = null,
    val cacheWriteTokensPath: String? = null,
)
