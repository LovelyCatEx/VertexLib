package com.lovelycatv.vertex.ai.workflow.graph.edge

/**
 * @author lovelycat
 * @since 2025-12-17 00:04
 * @version 1.0
 */
open class GraphEdge(
    val from: String,
    val to: String,
    val groupId: String = "DEFAULT_EDGE_GROUP"
)