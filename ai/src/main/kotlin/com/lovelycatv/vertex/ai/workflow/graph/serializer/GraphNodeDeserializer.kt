package com.lovelycatv.vertex.ai.workflow.graph.serializer

import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractSerializableGraphNode

/**
 * @author lovelycat
 * @since 2025-12-17 15:58
 * @version 1.0
 */
fun interface GraphNodeDeserializer<V: AbstractSerializableGraphNode> {
    fun deserialize(data: Map<String, Any>): V
}