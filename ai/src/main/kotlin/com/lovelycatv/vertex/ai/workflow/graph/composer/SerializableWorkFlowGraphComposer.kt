package com.lovelycatv.vertex.ai.workflow.graph.composer

import com.lovelycatv.vertex.ai.workflow.graph.AbstractSerializableWorkFlowGraph
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractSerializableGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeEntry
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeExit
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeType
import com.lovelycatv.vertex.ai.workflow.graph.node.condition.GraphNodeIf
import com.lovelycatv.vertex.ai.workflow.graph.node.math.GraphNodeAdd

/**
 * @author lovelycat
 * @since 2025-12-17 22:55
 * @version 1.0
 */
open class SerializableWorkFlowGraphComposer(
    graphFactory: () -> AbstractSerializableWorkFlowGraph<AbstractSerializableGraphNode>
) : KotlinWorkFlowGraphComposer<AbstractSerializableGraphNode, AbstractSerializableWorkFlowGraph<AbstractSerializableGraphNode>>(graphFactory) {
    fun entry(
        name: String = GraphNodeType.ENTRY.getTypeName(),
        inputs: GraphNodeInputParameterComposer.() -> Unit
    ): GraphNodeEntry {
        val entry = GraphNodeEntry(
            nodeName = name,
            inputs = GraphNodeInputParameterComposer().apply { inputs.invoke(this) }.build(),
            strict = true
        )
        this.graph.addNode(entry)
        return entry
    }

    fun exit(
        name: String = GraphNodeType.EXIT.getTypeName(),
        outputs: GraphNodeOutputParameterComposer.() -> Unit
    ): GraphNodeExit {
        val exit = GraphNodeExit(
            nodeName = name,
            outputs = GraphNodeOutputParameterComposer().apply { outputs.invoke(this) }.build(),
            strict = true
        )
        this.graph.addNode(exit)
        return exit
    }

    fun ifCondition(name: String = GraphNodeType.IF.getTypeName()): GraphNodeIf {
        val ifNode = GraphNodeIf(nodeName = name)
        this.graph.addNode(ifNode)
        return ifNode
    }

    fun add(name: String = GraphNodeType.ADD.getTypeName()): GraphNodeAdd {
        val addNode = GraphNodeAdd(nodeName = name)
        this.graph.addNode(addNode)
        return addNode
    }
}