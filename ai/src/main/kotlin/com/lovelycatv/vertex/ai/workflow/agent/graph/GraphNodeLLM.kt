package com.lovelycatv.vertex.ai.workflow.agent.graph

import com.lovelycatv.vertex.ai.openai.VertexAIClient
import com.lovelycatv.vertex.ai.openai.message.ChatMessage
import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-12-17 00:57
 * @version 1.0
 */
class GraphNodeLLM(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String,
    private val vertexAIClient: VertexAIClient,
    private val model: String
) : AbstractGraphNode(
    VertexAgentGraphNodeType.LLM,
    nodeId,
    nodeName,
    inputs = listOf(
        GraphNodeParameter(
            type = String::class,
            name = "systemPrompt"
        ),
        GraphNodeParameter(
            type = String::class,
            name = "userPrompt"
        )
    ),
    outputs = listOf(
        GraphNodeParameter(
            type = String::class,
            name = "content"
        )
    )
) {
    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        val systemPrompt = super.resolveParameterReference(inputData, "systemPrompt") as String?
        val userPrompt = super.resolveParameterReference(inputData, "userPrompt") as String

        val request = ChatCompletionRequest(
            model = model,
            messages = listOfNotNull(
                systemPrompt?.let {
                    ChatMessage.system(super.resolveParameters(inputData, systemPrompt))
                },
                ChatMessage.user(super.resolveParameters(inputData, userPrompt))
            ),
            stream = false
        )

        val response = this.vertexAIClient.chatCompletionBlocking(request)

        val outputs = this.outputs.associateWith {
            when (it.name) {
                "content" -> response.choices[0].message.content
                else -> null
            }
        }

        return outputs
    }
}