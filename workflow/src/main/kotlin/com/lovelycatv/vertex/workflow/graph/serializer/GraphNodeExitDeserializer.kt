package com.lovelycatv.vertex.workflow.graph.serializer

import com.lovelycatv.vertex.reflect.ReflectUtils
import com.lovelycatv.vertex.workflow.graph.node.GraphNodeExit

/**
 * @author lovelycat
 * @since 2025-12-17 15:58
 * @version 1.0
 */
class GraphNodeExitDeserializer : GraphNodeDeserializer<GraphNodeExit<*>> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(data: Map<String, Any>): GraphNodeExit<*> {
        return GraphNodeExit(
            data["nodeId"] as String,
            data["nodeName"] as String,
            ReflectUtils.classForNameIncludingPrimitiveTypes(data["outputValueType"] as String).kotlin,
            (data["strict"] as String).toBoolean()
        )
    }
}