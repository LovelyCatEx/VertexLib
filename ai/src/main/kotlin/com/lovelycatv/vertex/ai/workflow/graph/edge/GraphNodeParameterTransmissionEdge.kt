package com.lovelycatv.vertex.ai.workflow.graph.edge

/**
 * @author lovelycat
 * @since 2025-12-17 00:10
 * @version 1.0
 */
class GraphNodeParameterTransmissionEdge(
    from: String,
    to: String,
    val fromParameterName: String,
    val toParameterName: String,
    groupId: String = "DEFAULT_EDGE_GROUP"
) : GraphEdge(from, to, groupId)