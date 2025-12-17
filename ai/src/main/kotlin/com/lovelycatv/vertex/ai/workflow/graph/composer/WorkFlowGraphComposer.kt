package com.lovelycatv.vertex.ai.workflow.graph.composer

import com.lovelycatv.vertex.ai.workflow.graph.AbstractSerializableWorkFlowGraph
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractSerializableGraphNode

/**
 * @author lovelycat
 * @since 2025-12-17 22:36
 * @version 1.0
 */
class WorkFlowGraphComposer(
    graphFactory: () -> AbstractSerializableWorkFlowGraph<AbstractSerializableGraphNode>
) : SerializableWorkFlowGraphComposer(graphFactory)