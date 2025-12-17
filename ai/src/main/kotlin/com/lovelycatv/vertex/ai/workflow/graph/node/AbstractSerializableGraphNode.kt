package com.lovelycatv.vertex.ai.workflow.graph.node

/**
 * @author lovelycat
 * @since 2025-12-17 15:09
 * @version 1.0
 */
abstract class AbstractSerializableGraphNode(
    nodeType: IGraphNodeType,
    nodeId: String,
    nodeName: String,
    inputs: List<GraphNodeParameter>,
    outputs: List<GraphNodeParameter>
) : AbstractGraphNode(nodeType, nodeId, nodeName, inputs, outputs) {
    open fun serialize(): Map<String, Any> {
        return mapOf(
            "nodeType" to nodeType.getTypeName(),
            "nodeId" to nodeId,
            "nodeName" to nodeName,
            "inputs" to inputs.map { it.serialize() },
            "outputs" to outputs.map { it.serialize() }
        )
    }
}