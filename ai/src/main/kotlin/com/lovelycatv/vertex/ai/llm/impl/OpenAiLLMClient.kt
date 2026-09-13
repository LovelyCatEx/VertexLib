package com.lovelycatv.vertex.ai.llm.impl

import com.jayway.jsonpath.JsonPath
import com.lovelycatv.vertex.ai.llm.ChatRequest
import com.lovelycatv.vertex.ai.llm.ChatResponse
import com.lovelycatv.vertex.ai.llm.ErrorChatResponse
import com.lovelycatv.vertex.ai.llm.ErrorStreamChatResponse
import com.lovelycatv.vertex.ai.llm.LLMClient
import com.lovelycatv.vertex.ai.llm.LLMModel
import com.lovelycatv.vertex.ai.llm.ReasoningEffort
import com.lovelycatv.vertex.ai.llm.config.LLMClientConfig
import com.lovelycatv.vertex.ai.llm.StreamChatResponse
import com.lovelycatv.vertex.ai.llm.message.AssistantChatMessage
import com.lovelycatv.vertex.ai.llm.message.ChatMessage
import com.lovelycatv.vertex.ai.llm.message.StopReason
import com.lovelycatv.vertex.ai.llm.message.SystemChatMessage
import com.lovelycatv.vertex.ai.llm.message.ToolCall
import com.lovelycatv.vertex.ai.llm.message.ToolChatMessage
import com.lovelycatv.vertex.ai.llm.message.UserChatMessage
import com.lovelycatv.vertex.ai.llm.tool.ToolDeclaration
import com.lovelycatv.vertex.ai.llm.tool.parameter.ArrayToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.ObjectToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.PrimitiveToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.RequirableToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.ToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.ToolParameterType
import com.lovelycatv.vertex.ai.utils.tryRead

open class OpenAiLLMClient(llmClientConfig: LLMClientConfig) : LLMClient(llmClientConfig) {
    override fun transformRequestBody(chatRequest: ChatRequest): String {
        val requestMap = gson.toJson(
            mapOf(
                "model" to chatRequest.model,
                "messages" to parseMessagesList(chatRequest.messages),
                "stream" to chatRequest.stream,
                "tools" to chatRequest.tools?.let { parseToolsList(it) },
                "max_completion_tokens" to chatRequest.maxCompletionTokens,
                "temperature" to chatRequest.temperature,
                "top_p" to chatRequest.topP,
                "presence_penalty" to chatRequest.presencePenalty,
                "frequency_penalty" to chatRequest.frequencyPenalty,
                // `top_logprobs` only applies alongside `logprobs`, so asking for it implies the flag.
                "logprobs" to (chatRequest.logprobs ?: chatRequest.topLogprobs?.let { true }),
                "top_logprobs" to chatRequest.topLogprobs,
            ) + resolveReasoningConfig(chatRequest.reasoningEffort) + chatRequest.extraBody.orEmpty()
        )

        return requestMap
    }

    /**
     * Maps [ReasoningEffort] onto `reasoning_effort`.
     *
     * [ReasoningEffort.DISABLED] becomes `"none"`, the only value that actually turns reasoning
     * off — omitting the parameter leaves it on with the model's default effort. Conversely
     * [ReasoningEffort.AUTO] omits it so the provider picks. Neither is safe everywhere: models
     * before GPT-5.1 reject `"none"` and only some support `"xhigh"`, so targeting one of those
     * means choosing [ReasoningEffort.AUTO] or overriding via [ChatRequest.extraBody].
     */
    override fun resolveReasoningConfig(reasoningEffort: ReasoningEffort): Map<String, Any?> {
        val effort = when (reasoningEffort) {
            ReasoningEffort.DISABLED -> "none"
            ReasoningEffort.AUTO -> null
            ReasoningEffort.MINIMAL -> "minimal"
            ReasoningEffort.LOW -> "low"
            ReasoningEffort.MEDIUM -> "medium"
            ReasoningEffort.HIGH -> "high"
            // The scale tops out at "xhigh" here; there is no separate "max" level.
            ReasoningEffort.EXTRA_HIGH, ReasoningEffort.MAX -> "xhigh"
        }

        // Omitted entirely rather than sent as null, which Gson would drop anyway.
        return effort?.let { mapOf("reasoning_effort" to it) } ?: emptyMap()
    }

    fun parseMessagesList(messages: List<ChatMessage>): List<Map<String, Any?>> {
        return messages.map {
            when (it) {
                is UserChatMessage -> {
                    mapOf(
                        "role" to "user",
                        "content" to it.content
                    )
                }

                is AssistantChatMessage -> {
                    mapOf(
                        "role" to "assistant",
                        "content" to it.content,
                        "tool_calls" to it.toolCalls?.map {
                            mapOf(
                                "id" to it.id,
                                "type" to "function",
                                "function" to mapOf(
                                    "name" to it.toolName,
                                    "arguments" to it.originalArguments
                                )
                            )
                        }
                    )
                }

                is SystemChatMessage -> {
                    mapOf(
                        "role" to "system",
                        "content" to it.content
                    )
                }

                is ToolChatMessage -> {
                    mapOf(
                        "role" to "tool",
                        "tool_call_id" to it.toolCallId,
                        "content" to it.content
                    )
                }

                else -> throw UnsupportedOperationException("Unsupported message type: ${it.type.name}")
            }
        }
    }

    fun parseToolsList(tools: List<ToolDeclaration>): List<Map<String, Any?>> {
        return tools.map {
            mapOf(
                "type" to "function",
                "function" to mapOf(
                    "name" to it.name,
                    "description" to it.description,
                    "parameters" to parseParameter(
                        ObjectToolParameter(
                            "",
                            "",
                            it.parameters,
                            strict = true,
                            additionalProperties = false
                        )
                    )
                )
            )
        }
    }

    private fun parseParameter(toolParameter: ToolParameter): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "type" to parseToolParameterTypeName(toolParameter.type),
            "description" to toolParameter.description,
        )

        when (toolParameter) {
            is PrimitiveToolParameter -> {
            }

            is ArrayToolParameter -> {
                map["items"] = parseParameter(toolParameter.itemType)
            }

            is ObjectToolParameter -> {
                map["properties"] = toolParameter.properties
                    .associateBy { it.name }
                    .mapValues { parseParameter(it.value) }

                map["required"] = toolParameter.properties.mapNotNull {
                    if (it is RequirableToolParameter && (it as RequirableToolParameter).required) {
                        it.name
                    } else {
                        null
                    }
                }

                map["strict"] to toolParameter.strict

                map["additionalProperties"] to if (toolParameter.strict)
                    false
                else
                    toolParameter.additionalProperties
            }
        }

        return map
    }

    private fun parseToolParameterTypeName(type: ToolParameterType): String {
        return when (type) {
            ToolParameterType.STRING -> type.name.lowercase()
            ToolParameterType.INTEGER -> type.name.lowercase()
            ToolParameterType.NUMBER -> type.name.lowercase()
            ToolParameterType.BOOLEAN -> type.name.lowercase()
            ToolParameterType.ARRAY -> type.name.lowercase()
            ToolParameterType.OBJECT -> type.name.lowercase()
        }
    }

    override fun resolveChatResponse(ctx: ChatResponseResolveContext): ChatResponse {
        val errorResponse = isErrorResponse(ctx)
        if (errorResponse != null) {
            return errorResponse
        }

        return ChatResponse(
            success = true,
            id = ctx.responseJsonPath.tryRead("$.id") ?: "",
            timestamp = ctx.responseMap["created"]?.toString()?.toDouble()?.toLong() ?: 0L,
            model = ctx.responseMap["model"]?.toString() ?: ctx.request.model,
            choices = super.tryCast2StringMapList(ctx.responseMap["choices"])?.map { map ->
                val mapJsonPath = JsonPath.parse(gson.toJson(map))
                val stopReasonString = mapJsonPath.tryRead<String?>("$.finish_reason")
                val choiceIndex = mapJsonPath.tryRead<Number>("$.index")?.toInt() ?: 0

                ChatResponse.Choice(
                    index = choiceIndex,
                    message = AssistantChatMessage(
                        content = mapJsonPath.tryRead("$.message.content"),
                        reasoningContent = mapJsonPath.tryRead("$.message.reasoning_content"),
                        toolCalls = mapJsonPath.tryRead<List<Map<String, Any?>>?>("$.message.tool_calls")?.map { toolCallMap ->
                            val toolCallJsonPath = JsonPath.parse(gson.toJson(toolCallMap))

                            val toolIndex = toolCallJsonPath.tryRead<Number>("$.index")?.toInt() ?: 0
                            val originalArguments = toolCallJsonPath.tryRead("$.function.arguments") ?: "{}"
                            val arguments = gson.fromJson(originalArguments, Map::class.java)

                            ToolCall(
                                index = toolIndex,
                                id = toolCallMap["id"]?.toString() ?: "",
                                toolName = toolCallJsonPath.tryRead("$.function.name") ?: "",
                                arguments = super.tryCast2StringMap(arguments) ?: emptyMap(),
                                originalArguments = originalArguments,
                                streaming = false,
                            )
                        },
                        stopReason = this.routeStopReason(stopReasonString),
                        stopReasonString = stopReasonString,
                    )
                )
            } ?: emptyList(),
            usage = super.resolveUsage(ctx),
            originalResponse = ctx.responseBody
        )
    }

    /** {
     *    requestId -> {
     *      choices -> {
     *        toolCallIndex -> toolCalls
     *      }
     *    }
     *  }
     */
    private val streamingToolCallsMap = mutableMapOf<String, MutableMap<Int, MutableMap<Int, ToolCall>>>()
    override fun resolveStreamChatResponse(ctx: ChatResponseResolveContext): StreamChatResponse {
        val errorResponse = isStreamErrorResponse(ctx)
        if (errorResponse != null) {
            return errorResponse
        }

        val id = ctx.responseJsonPath.tryRead("$.id") ?: ""

        val choices = try {
            super.tryCast2StringMapList(ctx.responseMap["choices"])?.map { map ->
                val mapJsonPath = JsonPath.parse(gson.toJson(map))
                val stopReasonString = mapJsonPath.tryRead<String?>("$.finish_reason")
                val choiceIndex = mapJsonPath.tryRead<Number>("$.index")?.toInt() ?: 0

                ChatResponse.Choice(
                    index = choiceIndex,
                    message = AssistantChatMessage(
                        content = mapJsonPath.tryRead("$.delta.content"),
                        reasoningContent = mapJsonPath.tryRead("$.delta.reasoning_content"),
                        toolCalls = mapJsonPath.tryRead<List<Map<String, Any?>>?>("$.delta.tool_calls")?.map { toolCallMap ->
                            val toolCallJsonPath = JsonPath.parse(gson.toJson(toolCallMap))

                            val toolIndex = toolCallJsonPath.tryRead<Number>("$.index")?.toInt() ?: 0
                            val originalArguments = toolCallJsonPath.tryRead("$.function.arguments") ?: "{}"

                            val choiceToolCallsMap = streamingToolCallsMap.getOrPut(id) {
                                mutableMapOf()
                            }.getOrPut(choiceIndex) {
                                mutableMapOf()
                            }

                            val resolvedToolCall = ToolCall(
                                index = toolIndex,
                                id = toolCallMap["id"]?.toString() ?: "",
                                toolName = toolCallJsonPath.tryRead("$.function.name") ?: "",
                                arguments = emptyMap(),
                                originalArguments = originalArguments,
                                streaming = true,
                            )

                            val buffer = choiceToolCallsMap.getOrPut(toolIndex) {
                                resolvedToolCall
                            }

                            val bufferAfter = buffer.copy(
                                originalArguments = buffer.originalArguments + resolvedToolCall.originalArguments
                            )

                            choiceToolCallsMap[toolIndex] = bufferAfter

                            if (bufferAfter.originalArguments.endsWith("}")) {
                                bufferAfter.copy(
                                    arguments = super.tryCast2StringMap(
                                        gson.fromJson(bufferAfter.originalArguments, Map::class.java)
                                    ) ?: emptyMap(),
                                    streaming = false
                                )
                            } else {
                                bufferAfter
                            }
                        },
                        stopReason = this.routeStopReason(stopReasonString),
                        stopReasonString = stopReasonString,
                    )
                )
            } ?: emptyList()
        } catch (e: Exception) {
            // Prevent memory leak
            streamingToolCallsMap.remove(id)
            throw e
        }

        val finished = choices.all { it.message.stopReasonString != null }
        if (finished) {
            streamingToolCallsMap.remove(id)
        }

        return StreamChatResponse(
            id = id,
            timestamp = ctx.responseMap["created"]?.toString()?.toDouble()?.toLong() ?: 0L,
            model = ctx.responseMap["model"]?.toString() ?: ctx.request.model,
            choices = choices,
            usage = super.resolveUsage(ctx),
            finished = finished,
            originalResponse = ctx.responseBody
        )
    }

    override fun routeStopReason(stopReasonString: String?): StopReason? {
        if (stopReasonString == null) {
            return null
        }

        return when (stopReasonString) {
            "stop" -> StopReason.END_TURN
            "length" -> StopReason.REACHED_MAX_TOKENS
            "tool_calls" -> StopReason.TOOL_CALLS
            "content_filter" -> StopReason.CONTENT_FILTER
            else -> StopReason.OTHER
        }
    }

    override fun resolveModelsPage(responseBody: String): LLMModelPage {
        return LLMModelPage(
            modelsDataOf(parseModelsResponse(responseBody)).map {
                LLMModel(
                    id = it["id"]?.toString() ?: "",
                    // Epoch seconds. Not every OpenAI-compatible endpoint reports it.
                    createdAt = (it["created"] as? Number)?.toLong(),
                    ownedBy = it["owned_by"]?.toString(),
                )
            }
        )
    }

    private fun isErrorResponse(ctx: ChatResponseResolveContext) : ErrorChatResponse? {
        val errorMessage =  ctx.responseJsonPath.tryRead<String?>(
            llmClientConfig.llmResponseConfig.chatCompletions?.errorMessageJsonPath ?: "$.error.message"
        )

        return if (errorMessage != null) {
            ErrorChatResponse(errorMessage, ctx.responseBody)
        } else {
            null
        }
    }

    private fun isStreamErrorResponse(ctx: ChatResponseResolveContext) : ErrorStreamChatResponse? {
        val errorMessage = ctx.responseJsonPath.tryRead<String?>(
            llmClientConfig.llmResponseConfig.chatCompletions?.errorMessageJsonPath ?: "$.error.message"
        )

        return if (errorMessage != null) {
            ErrorStreamChatResponse(errorMessage, ctx.responseBody)
        } else {
            null
        }
    }
}