package com.lovelycatv.vertex.ai.agent.graph

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.openai.VertexAIClient
import com.lovelycatv.vertex.ai.utils.parseObject
import com.lovelycatv.vertex.workflow.graph.serializer.GraphNodeDeserializer

/**
 * @author lovelycat
 * @since 2025-12-17 17:20
 * @version 1.0
 */
class GraphNodeLLMDeserializer : GraphNodeDeserializer<GraphNodeLLM> {
    private val gson = Gson()

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeLLM {
        return GraphNodeLLM(
            data["nodeId"] as String,
            data["nodeName"] as String,
            VertexAIClient(
                (data["vertexAIClient"] as Map<String, Any>).parseObject(gson)
            )
        )
    }
}