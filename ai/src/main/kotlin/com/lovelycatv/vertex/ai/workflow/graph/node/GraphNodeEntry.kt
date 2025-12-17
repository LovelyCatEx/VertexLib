package com.lovelycatv.vertex.ai.workflow.graph.node

import java.util.*

/**
 * @author lovelycat
 * @since 2025-12-17 00:02
 * @version 1.0
 */
class GraphNodeEntry(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String,
    inputs: List<GraphNodeParameter>,
    private val strict: Boolean = false
) : BaseGraphNode(GraphNodeType.ENTRY, nodeId, nodeName, inputs, inputs) {
    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        return if (!strict) {
            inputData
        } else {
            inputs.associateWith {
                inputData[it]
            }
        }
    }

    override fun serialize(): Map<String, Any> {
        return super.serialize() + mapOf("strict" to strict.toString())
    }
}