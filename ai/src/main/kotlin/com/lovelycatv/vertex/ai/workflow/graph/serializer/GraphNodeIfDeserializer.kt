package com.lovelycatv.vertex.ai.workflow.graph.serializer

import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeIf

/**
 * @author lovelycat
 * @since 2025-12-17 15:58
 * @version 1.0
 */
class GraphNodeIfDeserializer : GraphNodeDeserializer<GraphNodeIf> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeIf {
        return GraphNodeIf(
            data["nodeId"] as String,
            data["nodeName"] as String
        )
    }
}