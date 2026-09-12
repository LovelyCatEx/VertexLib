package com.lovelycatv.vertex.ai.llm.config

import com.lovelycatv.vertex.ai.llm.LLMUsageResolverJsonPathConfig

data class LLMEndpointResponseConfig(
    val errorMessageJsonPath: String? = null,
    val usage: LLMUsageResolverJsonPathConfig? = null,
)
