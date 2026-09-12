package com.lovelycatv.vertex.ai.llm.config

data class LLMResponseConfig(
    val chatCompletions: LLMEndpointResponseConfig? = null,
    val embedding: LLMEndpointResponseConfig? = null,
)
