package com.lovelycatv.vertex.workflow.graph.serializer

import com.lovelycatv.vertex.workflow.graph.node.math.GraphNodeNumberComparator

/**
 * @author lovelycat
 * @since 2025-12-17 15:58
 * @version 1.0
 */
class GraphNodeNumberComparatorDeserializer : GraphNodeDeserializer<GraphNodeNumberComparator> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeNumberComparator {
        return GraphNodeNumberComparator(
            data["nodeId"] as String,
            data["nodeName"] as String,
            GraphNodeNumberComparator.Type.valueOf(data["type"] as String)
        )
    }
}