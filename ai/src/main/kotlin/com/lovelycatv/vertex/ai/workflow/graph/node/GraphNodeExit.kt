package com.lovelycatv.vertex.ai.workflow.graph.node

import java.util.*

/**
 * @author lovelycat
 * @since 2025-12-17 00:02
 * @version 1.0
 */
class GraphNodeExit(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String,
    outputs: List<GraphNodeParameter>,
    private val strict: Boolean = false
) : AbstractGraphNode(GraphNodeType.EXIT, nodeId, nodeName, outputs, outputs) {
    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        return if (!strict) {
            inputData
        } else {
            outputs.associateWith {
                inputData[it]
            }
        }
    }
}