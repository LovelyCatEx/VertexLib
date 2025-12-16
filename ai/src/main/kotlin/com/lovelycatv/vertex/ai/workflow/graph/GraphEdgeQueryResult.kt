package com.lovelycatv.vertex.ai.workflow.graph

import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphNodeParameterTransmissionEdge
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractGraphNode

/**
 * @author lovelycat
 * @since 2025-12-17 00:06
 * @version 1.0
 */
data class GraphEdgeQueryResult(
    val node: AbstractGraphNode,
    val triggerOrigins: List<String>,
    val triggerTargets: Map<String, List<String>>,
    val parameterOrigins: Map<String, GraphNodeParameterTransmissionEdge?>,
    val parameterOutputs: Map<String, List<GraphNodeParameterTransmissionEdge>>
)
