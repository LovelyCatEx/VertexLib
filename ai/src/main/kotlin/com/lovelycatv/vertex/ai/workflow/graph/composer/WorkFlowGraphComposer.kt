package com.lovelycatv.vertex.ai.workflow.graph.composer

import com.lovelycatv.vertex.ai.workflow.graph.AbstractSerializableWorkFlowGraph
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractSerializableGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeEntry
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeExit
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeType
import com.lovelycatv.vertex.ai.workflow.graph.node.condition.GraphNodeIf
import com.lovelycatv.vertex.ai.workflow.graph.node.math.*
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-12-17 22:36
 * @version 1.0
 */
class WorkFlowGraphComposer<R: Any>(
    graphFactory: () -> AbstractSerializableWorkFlowGraph<AbstractSerializableGraphNode, R>
) : SerializableWorkFlowGraphComposer<R>(graphFactory) {
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

    fun <R: Any> exit(
        name: String = GraphNodeType.EXIT.getTypeName(),
        outputType: KClass<R>
    ): GraphNodeExit<R> {
        val exit = GraphNodeExit(
            nodeName = name,
            outputValueType = outputType,
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

    fun <V: AbstractSerializableGraphNode> GraphNodeIf.triggerIfTrue(target: V) {
        this.trigger(target, GraphNodeIf.GROUP_PASSED)
    }

    fun <V: AbstractSerializableGraphNode> GraphNodeIf.triggerIfFalse(target: V) {
        this.trigger(target, GraphNodeIf.GROUP_FAILED)
    }

    fun add(name: String = GraphNodeType.ADD.getTypeName()): GraphNodeAdd {
        val addNode = GraphNodeAdd(nodeName = name)
        this.graph.addNode(addNode)
        return addNode
    }

    fun subtract(name: String = GraphNodeType.SUB.getTypeName()): GraphNodeSub {
        val subNode = GraphNodeSub(nodeName = name)
        this.graph.addNode(subNode)
        return subNode
    }

    fun multiply(name: String = GraphNodeType.MUL.getTypeName()): GraphNodeMul {
        val mulNode = GraphNodeMul(nodeName = name)
        this.graph.addNode(mulNode)
        return mulNode
    }

    fun divide(name: String = GraphNodeType.DIV.getTypeName()): GraphNodeDiv {
        val divNode = GraphNodeDiv(nodeName = name)
        this.graph.addNode(divNode)
        return divNode
    }

    fun compareNumber(
        name: String = GraphNodeType.NUMBER_COMPARATOR.getTypeName(),
        type: GraphNodeNumberComparator.Type
    ): GraphNodeNumberComparator {
        val comparator = GraphNodeNumberComparator(nodeName = name, type = type)
        this.graph.addNode(comparator)
        return comparator
    }
}