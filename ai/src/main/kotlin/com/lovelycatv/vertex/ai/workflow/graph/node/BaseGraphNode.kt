package com.lovelycatv.vertex.ai.workflow.graph.node

/**
 * @author lovelycat
 * @since 2025-12-17 23:12
 * @version 1.0
 */
abstract class BaseGraphNode(
    nodeType: IGraphNodeType,
    nodeId: String,
    nodeName: String,
    inputs: List<GraphNodeParameter>,
    outputs: List<GraphNodeParameter>
) : AbstractSerializableGraphNode(nodeType, nodeId, nodeName, inputs, outputs)