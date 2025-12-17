package com.lovelycatv.vertex.ai.workflow.graph

import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractSerializableGraphNode

/**
 * @author lovelycat
 * @since 2025-12-16 23:58
 * @version 1.0
 */
abstract class AbstractSerializableWorkFlowGraph : AbstractWorkFlowGraph<AbstractSerializableGraphNode>() {
    fun serialize(): Map<String, Any> {
        return mapOf(
            "graphNodeMap" to this.graphNodeMap.mapValues { it.value.serialize() },
            "graphNodeTriggerEdges" to this.graphNodeTriggerEdges,
            "graphNodeParameterTransmissionEdges" to this.graphNodeParameterTransmissionEdges
        )
    }
}