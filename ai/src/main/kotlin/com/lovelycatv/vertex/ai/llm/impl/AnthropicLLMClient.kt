package com.lovelycatv.vertex.ai.llm.impl

import com.jayway.jsonpath.JsonPath
import com.lovelycatv.vertex.ai.llm.ChatRequest
import com.lovelycatv.vertex.ai.llm.ChatResponse
import com.lovelycatv.vertex.ai.llm.ErrorChatResponse
import com.lovelycatv.vertex.ai.llm.ErrorStreamChatResponse
import com.lovelycatv.vertex.ai.llm.LLMClient
import com.lovelycatv.vertex.ai.llm.ReasoningEffort
import com.lovelycatv.vertex.ai.llm.StreamChatResponse
import com.lovelycatv.vertex.ai.llm.config.LLMClientConfig
import com.lovelycatv.vertex.ai.llm.config.LLMResponseConfigDefaults
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
import com.lovelycatv.vertex.ai.utils.tryRead
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Chat client for the Anthropic Messages API (`POST /v1/messages`).
 *
 * The wire format differs from the OpenAI-compatible one in ways this class absorbs:
 *  - `system` is a top-level parameter, not a message, so every [SystemChatMessage] is hoisted
 *    into it regardless of where it sits in the list.
 *  - `max_tokens` is required; it falls back to [DEFAULT_MAX_TOKENS] when
 *    [ChatRequest.maxCompletionTokens] is unset.
 *  - `content` is a list of typed blocks rather than a plain string, and tool results live in
 *    `tool_result` blocks inside `user` messages.
 *  - Tool declarations carry a bare JSON schema in `input_schema` instead of `function.parameters`.
 *  - The SSE stream has no `[DONE]` sentinel — it ends on a `message_stop` event.
 */
class AnthropicLLMClient(
    llmClientConfig: LLMClientConfig,
    /**
     * Emits `strict: true` on every tool declaration so the provider validates `tool_use.input`
     * against the schema. Turn it off for models that predate strict tool use.
     */
    private val strictTools: Boolean = true,
) : LLMClient(llmClientConfig) {

    init {
        // `chatCompletionPath` defaults to the OpenAI route. No Anthropic-compatible server serves
        // a Messages-shaped body there, so every request would come back as a confusing 400 about
        // the payload — failing here instead names the actual problem.
        require(llmClientConfig.chatCompletionPath != OPENAI_CHAT_COMPLETIONS_PATH) {
            "AnthropicLLMClient posts a Messages body, but chatCompletionPath is still the OpenAI " +
                "default \"$OPENAI_CHAT_COMPLETIONS_PATH\". Set chatCompletionPath = " +
                "\"$MESSAGES_PATH\" and llmResponseConfig = LLMResponseConfigDefaults.ANTHROPIC " +
                "on LLMClientConfig."
        }
    }

    override fun transformRequestBody(chatRequest: ChatRequest): String {
        return gson.toJson(
            mapOf(
                "model" to chatRequest.model,
                "max_tokens" to (chatRequest.maxCompletionTokens ?: DEFAULT_MAX_TOKENS),
                "stream" to chatRequest.stream,
                "system" to extractSystemPrompt(chatRequest.messages),
                "messages" to parseMessagesList(chatRequest.messages),
                "tools" to chatRequest.tools?.let { parseToolsList(it) },
                "temperature" to chatRequest.temperature,
                "top_p" to chatRequest.topP,
            ) + resolveReasoningConfig(chatRequest.reasoningEffort)
        )
    }

    /** Null values are dropped by Gson, so the parameter is only sent when it is actually set. */
    private fun extractSystemPrompt(messages: List<ChatMessage>): String? {
        return messages.filterIsInstance<SystemChatMessage>()
            .joinToString("\n\n") { it.content }
            .ifEmpty { null }
    }

    /**
     * Maps [ReasoningEffort] onto the `thinking` / `output_config.effort` pair.
     *
     * [ReasoningEffort.DISABLED] omits `thinking` entirely instead of sending
     * `{"type": "disabled"}` — some models reject an explicit disable.
     */
    private fun resolveReasoningConfig(reasoningEffort: ReasoningEffort): Map<String, Any?> {
        if (reasoningEffort == ReasoningEffort.DISABLED) {
            return emptyMap()
        }

        val effort = when (reasoningEffort) {
            ReasoningEffort.MINIMAL, ReasoningEffort.LOW -> "low"
            ReasoningEffort.MEDIUM -> "medium"
            ReasoningEffort.HIGH -> "high"
            ReasoningEffort.EXTRA_HIGH -> "xhigh"
            ReasoningEffort.MAX -> "max"
            ReasoningEffort.DISABLED, ReasoningEffort.AUTO -> null
        }

        return buildMap {
            put("thinking", mapOf("type" to "adaptive"))
            if (effort != null) {
                put("output_config", mapOf("effort" to effort))
            }
        }
    }

    fun parseMessagesList(messages: List<ChatMessage>): List<Map<String, Any?>> {
        val parsed = mutableListOf<Map<String, Any?>>()

        var index = 0

        while (index < messages.size) {
            when (val message = messages[index]) {
                is UserChatMessage -> {
                    parsed += mapOf(
                        "role" to "user",
                        "content" to listOf(mapOf("type" to CONTENT_BLOCK_TEXT, "text" to message.content))
                    )
                    index++
                }

                is AssistantChatMessage -> {
                    parsed += mapOf(
                        "role" to "assistant",
                        "content" to parseAssistantContent(message)
                    )
                    index++
                }

                is ToolChatMessage -> {
                    // Tool results are `user` turns carrying `tool_result` blocks, and the API
                    // rejects consecutive same-role messages — so every adjacent result is merged
                    // into a single turn, the way a parallel tool call expects it.
                    val blocks = mutableListOf<Map<String, Any?>>()

                    while (index < messages.size && messages[index] is ToolChatMessage) {
                        val toolMessage = messages[index] as ToolChatMessage
                        blocks += mapOf(
                            "type" to CONTENT_BLOCK_TOOL_RESULT,
                            "tool_use_id" to toolMessage.toolCallId,
                            "content" to toolMessage.content,
                        )
                        index++
                    }

                    parsed += mapOf("role" to "user", "content" to blocks)
                }

                // Hoisted into the top-level `system` parameter by transformRequestBody.
                is SystemChatMessage -> index++
            }
        }

        return parsed
    }

    private fun parseAssistantContent(message: AssistantChatMessage): List<Map<String, Any?>> {
        val blocks = mutableListOf<Map<String, Any?>>()

        message.content?.takeIf { it.isNotEmpty() }?.let {
            blocks += mapOf("type" to CONTENT_BLOCK_TEXT, "text" to it)
        }

        // `reasoningContent` is deliberately not replayed. Thinking blocks have to be echoed back
        // with the signature the model produced, and AssistantChatMessage does not carry one.

        message.toolCalls?.forEach {
            blocks += mapOf(
                "type" to CONTENT_BLOCK_TOOL_USE,
                "id" to it.id,
                "name" to it.toolName,
                "input" to it.arguments,
            )
        }

        return blocks
    }

    fun parseToolsList(tools: List<ToolDeclaration>): List<Map<String, Any?>> {
        return tools.map {
            mapOf(
                "name" to it.name,
                "description" to it.description,
                "input_schema" to parseParameter(
                    ObjectToolParameter(
                        "",
                        "",
                        it.parameters,
                        strict = strictTools,
                        additionalProperties = false
                    )
                ),
                "strict" to strictTools,
            )
        }
    }

    private fun parseParameter(toolParameter: ToolParameter): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "type" to toolParameter.type.name.lowercase(),
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

                map["required"] = toolParameter.properties
                    .filter { it is RequirableToolParameter && it.required }
                    .map { it.name }

                map["additionalProperties"] = if (toolParameter.strict)
                    false
                else
                    toolParameter.additionalProperties
            }
        }

        return map
    }

    override fun buildRequest(url: String, requestBody: RequestBody): Request {
        return Request.Builder()
            .url(url)
            .addHeader("x-api-key", llmClientConfig.apiKey)
            .post(requestBody)
            .build()
    }

    override fun resolveChatResponse(ctx: ChatResponseResolveContext): ChatResponse {
        isErrorResponse(ctx)?.let { return it }

        val stopReasonString = ctx.responseJsonPath.tryRead<String?>("$.stop_reason")

        return ChatResponse(
            success = true,
            id = ctx.responseJsonPath.tryRead("$.id") ?: "",
            // The Messages API returns no creation time, unlike the OpenAI-compatible one.
            timestamp = System.currentTimeMillis(),
            model = ctx.responseMap["model"]?.toString() ?: ctx.request.model,
            choices = listOf(
                ChatResponse.Choice(
                    index = 0,
                    message = parseAssistantMessage(
                        tryCast2StringMapList(ctx.responseMap["content"]) ?: emptyList(),
                        stopReasonString
                    )
                )
            ),
            usage = resolveUsage(ctx),
            originalResponse = ctx.responseBody
        )
    }

    private fun parseAssistantMessage(
        content: List<Map<String, Any?>>,
        stopReasonString: String?
    ): AssistantChatMessage {
        val text = StringBuilder()
        val thinking = StringBuilder()
        val toolCalls = mutableListOf<ToolCall>()

        content.forEach { block ->
            when (block["type"]) {
                CONTENT_BLOCK_TEXT -> text.append(block["text"]?.toString().orEmpty())
                CONTENT_BLOCK_THINKING -> thinking.append(block["thinking"]?.toString().orEmpty())
                CONTENT_BLOCK_TOOL_USE -> toolCalls += parseToolUseBlock(block, toolCalls.size)
            }
        }

        return deltaMessage(
            content = text.toString().ifEmpty { null },
            reasoningContent = thinking.toString().ifEmpty { null },
            toolCalls = toolCalls.ifEmpty { null },
            stopReasonString = stopReasonString,
        )
    }

    private fun parseToolUseBlock(block: Map<String, Any?>, index: Int): ToolCall {
        val input = block["input"]

        return ToolCall(
            index = index,
            id = block["id"]?.toString() ?: "",
            toolName = block["name"]?.toString() ?: "",
            arguments = tryCast2StringMap(input) ?: emptyMap(),
            originalArguments = gson.toJson(input ?: emptyMap<String, Any?>()),
            streaming = false,
        )
    }

    /**
     * Tool calls being accumulated from `input_json_delta` fragments, keyed by response id and then
     * by content block index.
     *
     * Anthropic only sends the response id on `message_start`, so [streamingResponseId] remembers
     * it for the events that reference the message by nothing but its block indices. Two streams
     * collected concurrently from one client instance would interleave these buffers.
     */
    private val streamingToolCallsMap = mutableMapOf<String, MutableMap<Int, ToolCall>>()
    private var streamingResponseId: String? = null
    private var streamingResponseModel: String? = null

    override fun resolveStreamChatResponse(ctx: ChatResponseResolveContext): StreamChatResponse {
        isStreamErrorResponse(ctx)?.let { return it }

        val eventType = ctx.responseJsonPath.tryRead<String?>("$.type").orEmpty()

        // `message_start` wraps the message — id, model and usage included — under `message`.
        // Every other event describes that message's content directly.
        val eventCtx = if (eventType == EVENT_MESSAGE_START) nestedContext(ctx, "message") else ctx

        if (eventType == EVENT_MESSAGE_START) {
            streamingResponseId = eventCtx.responseJsonPath.tryRead("$.id")
            streamingResponseModel = eventCtx.responseMap["model"]?.toString()
            streamingResponseId?.let { streamingToolCallsMap.remove(it) }
        }

        val responseId = eventCtx.responseJsonPath.tryRead("$.id")
            ?: streamingResponseId.orEmpty()

        val blockIndex = eventCtx.responseJsonPath.tryRead<Number>("$.index")?.toInt() ?: 0
        val stopReasonString = ctx.responseJsonPath.tryRead<String?>("$.delta.stop_reason")

        val message = when (eventType) {
            EVENT_MESSAGE_START -> deltaMessage()

            EVENT_CONTENT_BLOCK_START -> resolveContentBlockStart(eventCtx, responseId, blockIndex)

            EVENT_CONTENT_BLOCK_DELTA -> resolveContentBlockDelta(eventCtx, responseId, blockIndex)

            EVENT_CONTENT_BLOCK_STOP -> deltaMessage(
                toolCalls = streamingToolCallsMap[responseId]
                    ?.remove(blockIndex)
                    ?.let { listOf(finalizeToolCall(it)) }
            )

            EVENT_MESSAGE_DELTA -> deltaMessage(stopReasonString = stopReasonString)

            else -> deltaMessage()
        }

        // `message_delta` carries the stop reason and `message_stop` closes the stream; either one
        // means no further content blocks are coming.
        val finished = eventType == EVENT_MESSAGE_DELTA || eventType == EVENT_MESSAGE_STOP
        if (finished) {
            streamingResponseId?.let { streamingToolCallsMap.remove(it) }
            streamingResponseId = null
            streamingResponseModel = null
        }

        return StreamChatResponse(
            id = responseId,
            timestamp = System.currentTimeMillis(),
            model = ctx.responseMap["model"]?.toString()
                ?: streamingResponseModel
                ?: ctx.request.model,
            choices = listOf(ChatResponse.Choice(index = 0, message = message)),
            usage = resolveStreamUsage(eventCtx, eventType),
            finished = finished,
            originalResponse = ctx.responseBody
        )
    }

    /**
     * `message_start` reports the prompt usage for the whole stream, while `message_delta` reports
     * the final `output_tokens`. Only one half arrives per event, so pass the event's own document
     * along for the usage paths to be resolved against.
     */
    private fun resolveStreamUsage(
        eventCtx: ChatResponseResolveContext,
        eventType: String
    ): ChatResponse.Usage {
        if (eventType != EVENT_MESSAGE_START) {
            return resolveUsage(eventCtx)
        }

        return ChatResponse.Usage(
            promptTokens = readTokenCount(eventCtx, "input_tokens"),
            cachedPromptTokens = readTokenCount(eventCtx, "cache_read_input_tokens"),
            cacheCreationTokens = readTokenCount(eventCtx, "cache_creation_input_tokens"),
        )
    }

    private fun readTokenCount(ctx: ChatResponseResolveContext, field: String): Int {
        return ctx.responseJsonPath.tryRead<Number>("$.usage.$field")?.toInt() ?: 0
    }

    private fun resolveContentBlockStart(
        ctx: ChatResponseResolveContext,
        responseId: String,
        blockIndex: Int
    ): AssistantChatMessage {
        val contentBlock = tryCast2StringMap(ctx.responseMap["content_block"]) ?: emptyMap()

        return when (contentBlock["type"]) {
            CONTENT_BLOCK_TEXT -> deltaMessage(content = contentBlock["text"]?.toString())

            CONTENT_BLOCK_THINKING -> deltaMessage(reasoningContent = contentBlock["thinking"]?.toString())

            CONTENT_BLOCK_TOOL_USE -> {
                val toolCall = ToolCall(
                    index = blockIndex,
                    id = contentBlock["id"]?.toString() ?: "",
                    toolName = contentBlock["name"]?.toString() ?: "",
                    arguments = emptyMap(),
                    // Deltas append `partial_json` fragments onto this, so the buffer starts empty
                    // rather than with the placeholder `{}` that the start block carries.
                    originalArguments = "",
                    streaming = true,
                )

                streamingToolCallsMap.getOrPut(responseId) { mutableMapOf() }[blockIndex] = toolCall

                deltaMessage(toolCalls = listOf(toolCall))
            }

            else -> deltaMessage()
        }
    }

    private fun resolveContentBlockDelta(
        ctx: ChatResponseResolveContext,
        responseId: String,
        blockIndex: Int
    ): AssistantChatMessage {
        val delta = tryCast2StringMap(ctx.responseMap["delta"]) ?: emptyMap()

        return when (delta["type"]) {
            DELTA_TEXT -> deltaMessage(content = delta["text"]?.toString())

            DELTA_THINKING -> deltaMessage(reasoningContent = delta["thinking"]?.toString())

            DELTA_INPUT_JSON -> {
                val buffer = streamingToolCallsMap[responseId]?.get(blockIndex)
                    ?: return deltaMessage()

                val accumulated = buffer.copy(
                    originalArguments = buffer.originalArguments + delta["partial_json"]?.toString().orEmpty()
                )

                streamingToolCallsMap[responseId]?.set(blockIndex, accumulated)

                deltaMessage(toolCalls = listOf(accumulated))
            }

            // `signature_delta` closes a thinking block and carries nothing we can replay.
            else -> deltaMessage()
        }
    }

    private fun finalizeToolCall(toolCall: ToolCall): ToolCall {
        val arguments = try {
            tryCast2StringMap(gson.fromJson(toolCall.originalArguments, Map::class.java))
        } catch (_: Exception) {
            null
        }

        return toolCall.copy(arguments = arguments ?: emptyMap(), streaming = false)
    }

    private fun deltaMessage(
        content: String? = null,
        reasoningContent: String? = null,
        toolCalls: List<ToolCall>? = null,
        stopReasonString: String? = null,
    ): AssistantChatMessage {
        return AssistantChatMessage(
            content = content,
            reasoningContent = reasoningContent,
            toolCalls = toolCalls,
            stopReason = routeStopReason(stopReasonString),
            stopReasonString = stopReasonString,
        )
    }

    private fun nestedContext(ctx: ChatResponseResolveContext, field: String): ChatResponseResolveContext {
        val map = tryCast2StringMap(ctx.responseMap[field]) ?: emptyMap()

        return ChatResponseResolveContext(
            ctx.request,
            ctx.responseBody,
            map,
            JsonPath.parse(gson.toJson(map))
        )
    }

    override fun routeStopReason(stopReasonString: String?): StopReason? {
        return when (stopReasonString) {
            null -> null
            "end_turn" -> StopReason.END_TURN
            "max_tokens" -> StopReason.REACHED_MAX_TOKENS
            "tool_use" -> StopReason.TOOL_CALLS
            "refusal" -> StopReason.CONTENT_FILTER
            // "stop_sequence" simply ended the turn; "pause_turn" and
            // "model_context_window_exceeded" have no counterpart here.
            "stop_sequence" -> StopReason.END_TURN
            else -> StopReason.OTHER
        }
    }

    private fun isErrorResponse(ctx: ChatResponseResolveContext): ErrorChatResponse? {
        return readErrorMessage(ctx)?.let { ErrorChatResponse(it, ctx.responseBody) }
    }

    private fun isStreamErrorResponse(ctx: ChatResponseResolveContext): ErrorStreamChatResponse? {
        return readErrorMessage(ctx)?.let { ErrorStreamChatResponse(it, ctx.responseBody) }
    }

    private fun readErrorMessage(ctx: ChatResponseResolveContext): String? {
        return ctx.responseJsonPath.tryRead(
            llmClientConfig.llmResponseConfig.chatCompletions?.errorMessageJsonPath
                ?: DEFAULT_ERROR_MESSAGE_JSON_PATH
        )
    }

    companion object {
        const val DEFAULT_MAX_TOKENS = 4096

        private const val MESSAGES_PATH = "messages"

        private const val OPENAI_CHAT_COMPLETIONS_PATH = "chat/completions"

        private const val EVENT_MESSAGE_START = "message_start"
        private const val EVENT_CONTENT_BLOCK_START = "content_block_start"
        private const val EVENT_CONTENT_BLOCK_DELTA = "content_block_delta"
        private const val EVENT_CONTENT_BLOCK_STOP = "content_block_stop"
        private const val EVENT_MESSAGE_DELTA = "message_delta"
        private const val EVENT_MESSAGE_STOP = "message_stop"

        private const val CONTENT_BLOCK_TEXT = "text"
        private const val CONTENT_BLOCK_THINKING = "thinking"
        private const val CONTENT_BLOCK_TOOL_USE = "tool_use"
        private const val CONTENT_BLOCK_TOOL_RESULT = "tool_result"

        private const val DELTA_TEXT = "text_delta"
        private const val DELTA_THINKING = "thinking_delta"
        private const val DELTA_INPUT_JSON = "input_json_delta"

        private const val DEFAULT_ERROR_MESSAGE_JSON_PATH = "$.error.message"
    }
}
