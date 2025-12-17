package com.lovelycatv.vertex.workflow.graph.edge

/**
 * @author lovelycat
 * @since 2025-12-17 00:04
 * @version 1.0
 */
class GraphTriggerEdge(
    from: String,
    to: String,
    val groupId: String
) : GraphEdge(from, to)