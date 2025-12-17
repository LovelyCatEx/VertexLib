package com.lovelycatv.vertex.ai.workflow.graph.serializer

import com.lovelycatv.vertex.ai.workflow.graph.node.condition.GraphNodeStringContains

/**
 * @author lovelycat
 * @since 2025-12-18 02:28
 * @version 1.0
 */
class GraphNodeStringContainsDeserializer : GraphNodeDeserializer<GraphNodeStringContains> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeStringContains {
        return GraphNodeStringContains(
            data["nodeId"] as String,
            data["nodeName"] as String,
            GraphNodeStringContains.Type.valueOf(data["type"] as String)
        )
    }
}