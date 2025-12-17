package com.lovelycatv.vertex.workflow.graph.serializer

import com.lovelycatv.vertex.workflow.graph.node.math.GraphNodeDiv

/**
 * @author lovelycat
 * @since 2025-12-17 15:58
 * @version 1.0
 */
class GraphNodeDivDeserializer : GraphNodeDeserializer<GraphNodeDiv> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeDiv {
        return GraphNodeDiv(
            data["nodeId"] as String,
            data["nodeName"] as String
        )
    }
}