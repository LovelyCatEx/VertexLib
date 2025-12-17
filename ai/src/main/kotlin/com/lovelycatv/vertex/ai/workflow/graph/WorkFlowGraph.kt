package com.lovelycatv.vertex.ai.workflow.graph

import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractSerializableGraphNode

/**
 * @author lovelycat
 * @since 2025-12-16 23:58
 * @version 1.0
 */
open class WorkFlowGraph<R: Any>(
    graphName: String
) : AbstractSerializableWorkFlowGraph<AbstractSerializableGraphNode, R>(graphName)