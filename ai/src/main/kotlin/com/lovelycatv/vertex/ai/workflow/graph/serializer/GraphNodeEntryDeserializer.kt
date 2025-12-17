package com.lovelycatv.vertex.ai.workflow.graph.serializer

import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeEntry
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter

/**
 * @author lovelycat
 * @since 2025-12-17 15:58
 * @version 1.0
 */
class GraphNodeEntryDeserializer : GraphNodeDeserializer<GraphNodeEntry> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeEntry {
        return GraphNodeEntry(
            data["nodeId"] as String,
            data["nodeName"] as String,
            (data["inputs"] as List<Map<String, String>>).map { GraphNodeParameter.fromSerialized(it) },
            (data["strict"] as String).toBoolean()
        )
    }
}