package com.lovelycatv.vertex.ai.workflow.graph.composer

import com.lovelycatv.vertex.ai.workflow.graph.AbstractWorkFlowGraph
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter

/**
 * @author lovelycat
 * @since 2025-12-17 22:36
 * @version 1.0
 */
open class KotlinWorkFlowGraphComposer<V: AbstractGraphNode, G: AbstractWorkFlowGraph<V, R>, R: Any>(
    graphFactory: () -> G
) : BaseWorkFlowGraphComposer<V, G, R>(graphFactory) {
    infix fun <V: AbstractGraphNode> V.transmit(fromParameter: GraphNodeParameter): ParameterTransmissionFrom<V> {
        return ParameterTransmissionFrom(this, fromParameter.name)
    }

    infix fun <V: AbstractGraphNode> V.transmit(fromParameter: String): ParameterTransmissionFrom<V> {
        return ParameterTransmissionFrom(this, fromParameter)
    }

    infix fun <V: AbstractGraphNode> V.transmitTo(targetNode: V) {
        this.transmit(this.outputs.first(), targetNode, targetNode.inputs.first())
    }

    infix fun <V: AbstractGraphNode> ParameterTransmissionFrom<V>.to(targetParameter: GraphNodeParameter): ParameterTransmissionTo<V> {
        return ParameterTransmissionTo(this.from, this.parameterName, targetParameter.name)
    }

    infix fun <V: AbstractGraphNode> ParameterTransmissionFrom<V>.to(targetParameter: String): ParameterTransmissionTo<V> {
        return ParameterTransmissionTo(this.from, this.parameterName, targetParameter)
    }

    infix fun <V: AbstractGraphNode> ParameterTransmissionFrom<V>.to(targetNode: V) {
        this.from.transmit(this.parameterName, targetNode, targetNode.inputs.first())
    }

    infix fun <V: AbstractGraphNode> ParameterTransmissionTo<*>.inNode(targetNode: V) {
        this.from.transmit(this.parameterName, targetNode, this.toParameterName)
    }

    data class ParameterTransmissionFrom<V: AbstractGraphNode>(
        val from: V,
        val parameterName: String
    )

    data class ParameterTransmissionTo<V: AbstractGraphNode>(
        val from: V,
        val parameterName: String,
        val toParameterName: String
    )
}