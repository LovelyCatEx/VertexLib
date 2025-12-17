package com.lovelycatv.vertex.workflow.graph.serializer

import com.lovelycatv.vertex.workflow.graph.node.math.GraphNodeAdd

/**
 * @author lovelycat
 * @since 2025-12-17 15:58
 * @version 1.0
 */
class GraphNodeAddDeserializer : GraphNodeDeserializer<GraphNodeAdd> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeAdd {
        return GraphNodeAdd(
            data["nodeId"] as String,
            data["nodeName"] as String
        )
    }
}