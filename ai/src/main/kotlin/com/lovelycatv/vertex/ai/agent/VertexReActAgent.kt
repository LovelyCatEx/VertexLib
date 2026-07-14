package com.lovelycatv.vertex.ai.agent

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lovelycatv.vertex.ai.agent.tool.VertexToolRegistry
import com.lovelycatv.vertex.ai.openai.ChatMessageRole
import com.lovelycatv.vertex.ai.openai.VertexAIClient
import com.lovelycatv.vertex.ai.openai.message.ChatMessage
import com.lovelycatv.vertex.ai.openai.message.IChatMessage
import com.lovelycatv.vertex.ai.openai.message.ToolCallMessage
import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest

/**
 * A ReAct-style agent that resolves tool calls through a [VertexToolRegistry].
 *
 * The [initialRequest] still owns the tool list sent to the model, so callers decide which
 * tools are exposed — typically by passing [VertexToolRegistry.getTools]. When the model
 * emits a tool call, the agent looks the tool up in [toolRegistry], invokes it with the
 * parsed arguments, and feeds the result back as a [ToolCallMessage].
 */
class VertexReActAgent(
    val vertexAIClient: VertexAIClient,
    val systemPrompt: String,
    val initialRequest: ChatCompletionRequest,
    val toolRegistry: VertexToolRegistry = VertexToolRegistry()
) {
    private val gson = Gson()

    private val systemPromptMessage = ChatMessage(
        role = ChatMessageRole.ASSISTANT,
        content = systemPrompt
    )

    private val messages: MutableList<IChatMessage> = mutableListOf()

    suspend fun send(message: ChatMessage) {
        println(message.role.name + ": " + message.content)

        messages += message

        val resp = vertexAIClient.chatCompletionBlocking(
            initialRequest.copy(
                messages = listOf(systemPromptMessage) + messages
            )
        ).choices[0].message

        this.messages.add(resp)

        var cycleResp = resp

        println(cycleResp.role.name + ": " + (cycleResp.reasoningContent ?: cycleResp.content))

        while (!cycleResp.toolCalls.isNullOrEmpty()) {
            cycleResp.toolCalls.forEach {
                println("TOOL: ${it.function.name}, parameters: ${it.function.arguments}")
                messages.add(
                    ToolCallMessage(
                        role = ChatMessageRole.TOOL,
                        content = resolveToolCall(it.function.name, it.function.arguments),
                        toolCallId = it.id
                    )
                )
            }

            cycleResp = vertexAIClient.chatCompletionBlocking(
                initialRequest.copy(
                    messages = listOf(systemPromptMessage) + messages
                )
            ).choices[0].message

            this.messages.add(cycleResp)

            println(cycleResp.role.name + ": " + (cycleResp.reasoningContent ?: cycleResp.content))
        }
    }

    /**
     * Invokes the named tool through the registry with the model-provided JSON [arguments],
     * returning a string result for the tool message. Failures are captured as an error
     * string so the model can react instead of the whole cycle throwing.
     */
    private suspend fun resolveToolCall(name: String, arguments: String): String {
        return try {
            val parsed: Map<String, Any?> = if (arguments.isBlank()) {
                emptyMap()
            } else {
                gson.fromJson(arguments, object : TypeToken<Map<String, Any?>>() {}.type)
            }
            when (val result = toolRegistry.invoke(name, parsed)) {
                null -> "null"
                is String -> result
                is Char, Byte, Short, Int, Long, Float, Double, Boolean -> result.toString()
                else -> gson.toJson(result)
            }
        } catch (e: Exception) {
            "Tool '$name' failed: ${e.message}"
        }
    }
}
