package com.lovelycatv.vertex.ai.agent

import com.lovelycatv.vertex.ai.openai.ChatMessageRole
import com.lovelycatv.vertex.ai.openai.VertexAIClient
import com.lovelycatv.vertex.ai.openai.message.ChatMessage
import com.lovelycatv.vertex.ai.openai.message.IChatMessage
import com.lovelycatv.vertex.ai.openai.message.ToolCallMessage
import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest

class VertexReActAgent(
    val vertexAIClient: VertexAIClient,
    val systemPrompt: String,
    val initialRequest: ChatCompletionRequest
) {
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

        var cycleResp = resp

        println(cycleResp.role.name + ": " + (cycleResp.reasoningContent ?: cycleResp.content))

        while (!cycleResp.toolCalls.isNullOrEmpty()) {
            cycleResp.toolCalls.forEach {
                println("TOOL: ${it.function.name}, parameters: ${it.function.arguments}")
                messages.add(
                    ToolCallMessage(
                        role = ChatMessageRole.TOOL,
                        content = "unknown-result",
                        toolCallId = it.id
                    )
                )
            }

            cycleResp = vertexAIClient.chatCompletionBlocking(
                initialRequest.copy(
                    messages = listOf(systemPromptMessage) + messages
                )
            ).choices[0].message

            println(cycleResp.role.name + ": " + (cycleResp.reasoningContent ?: cycleResp.content))
        }


        this.messages.add(resp)
    }
}