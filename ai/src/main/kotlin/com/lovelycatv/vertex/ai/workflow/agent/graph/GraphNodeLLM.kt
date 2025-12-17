package com.lovelycatv.vertex.ai.workflow.agent.graph

import com.lovelycatv.vertex.ai.openai.VertexAIClient
import com.lovelycatv.vertex.ai.openai.message.ChatMessage
import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import com.lovelycatv.vertex.ai.workflow.graph.node.BaseGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import java.util.*

/**
 * @author lovelycat
 * @since 2025-12-17 00:57
 * @version 1.0
 */
class GraphNodeLLM(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String,
    private val vertexAIClient: VertexAIClient
) : BaseGraphNode(
    VertexAgentGraphNodeType.LLM,
    nodeId,
    nodeName,
    inputs = listOf(
        GraphNodeParameter(
            type = String::class,
            name = INPUT_SYSTEM_PROMPT
        ),
        GraphNodeParameter(
            type = String::class,
            name = INPUT_USER_PROMPT
        ),
        GraphNodeParameter(
            type = String::class,
            name = INPUT_MODEL
        )
    ),
    outputs = listOf(
        GraphNodeParameter(
            type = String::class,
            name = OUTPUT_CONTENT
        )
    )
) {
    companion object {
        const val INPUT_SYSTEM_PROMPT = "systemPrompt"
        const val INPUT_USER_PROMPT = "userPrompt"
        const val INPUT_MODEL = "model"
        const val OUTPUT_CONTENT = "content"
    }

    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        val systemPrompt = super.resolveParameterReference(inputData, INPUT_SYSTEM_PROMPT) as String?
        val userPrompt = super.resolveParameterReference(inputData, INPUT_USER_PROMPT) as String
        val model = super.resolveParameterReference(inputData, INPUT_MODEL) as String

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
                OUTPUT_CONTENT -> response.choices[0].message.content
                else -> null
            }
        }

        return outputs
    }

    override fun serialize(): Map<String, Any> {
        return super.serialize() + mapOf("vertexAIClient" to vertexAIClient.config)
    }
}