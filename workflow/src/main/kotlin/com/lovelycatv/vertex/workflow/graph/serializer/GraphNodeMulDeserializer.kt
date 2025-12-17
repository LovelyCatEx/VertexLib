package com.lovelycatv.vertex.workflow.graph.serializer

import com.lovelycatv.vertex.workflow.graph.node.math.GraphNodeMul

/**
 * @author lovelycat
 * @since 2025-12-17 15:58
 * @version 1.0
 */
class GraphNodeMulDeserializer : GraphNodeDeserializer<GraphNodeMul> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeMul {
        return GraphNodeMul(
            data["nodeId"] as String,
            data["nodeName"] as String
        )
    }
}