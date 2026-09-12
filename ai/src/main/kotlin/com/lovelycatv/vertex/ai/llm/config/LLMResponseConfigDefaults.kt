package com.lovelycatv.vertex.ai.llm.config

import com.lovelycatv.vertex.ai.llm.LLMUsageResolverJsonPathConfig

object LLMResponseConfigDefaults {
    val OPENAI = LLMResponseConfig(
        chatCompletions = LLMEndpointResponseConfig(
            errorMessageJsonPath = "$.error.message",
            usage = LLMUsageResolverJsonPathConfig(
                promptTokensPath = "$.usage.prompt_tokens",
                completionTokensPath = "$.usage.completion_tokens",
                reasoningTokensPath = "$.usage.completion_tokens_details.reasoning_tokens",
                cacheReadTokensPath = "$.usage.prompt_tokens_details.cached_tokens",
            ),
        ),
        embedding = LLMEndpointResponseConfig(
            usage = LLMUsageResolverJsonPathConfig(
                promptTokensPath = "$.usage.prompt_tokens",
            ),
        ),
    )

    val ANTHROPIC = LLMResponseConfig(
        chatCompletions = LLMEndpointResponseConfig(
            errorMessageJsonPath = "$.error.message",
            usage = LLMUsageResolverJsonPathConfig(
                promptTokensPath = "$.usage.input_tokens",
                completionTokensPath = "$.usage.output_tokens",
                reasoningTokensPath = "$.usage.output_tokens_details.thinking_tokens",
                cacheReadTokensPath = "$.usage.cache_read_input_tokens",
                cacheWriteTokensPath = "$.usage.cache_creation_input_tokens",
            ),
        ),
    )
}