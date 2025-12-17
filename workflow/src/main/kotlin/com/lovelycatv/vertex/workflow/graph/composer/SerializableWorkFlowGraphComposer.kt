package com.lovelycatv.vertex.workflow.graph.composer

import com.lovelycatv.vertex.workflow.graph.AbstractSerializableWorkFlowGraph
import com.lovelycatv.vertex.workflow.graph.node.AbstractSerializableGraphNode

/**
 * @author lovelycat
 * @since 2025-12-17 22:55
 * @version 1.0
 */
open class SerializableWorkFlowGraphComposer<R: Any>(
    graphFactory: () -> AbstractSerializableWorkFlowGraph<AbstractSerializableGraphNode, R>
) : KotlinWorkFlowGraphComposer<AbstractSerializableGraphNode, AbstractSerializableWorkFlowGraph<AbstractSerializableGraphNode, R>, R>(graphFactory)