package com.lovelycatv.vertex.ai.workflow.graph.composer

import com.lovelycatv.vertex.ai.workflow.graph.AbstractWorkFlowGraph
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractGraphNode

/**
 * @author lovelycat
 * @since 2025-12-17 22:16
 * @version 1.0
 */
open class BaseWorkFlowGraphComposer<V: AbstractGraphNode, G: AbstractWorkFlowGraph<V>>(
    graphFactory: () -> G
) {
    protected val graph = graphFactory.invoke()

    fun accessGraph(action: G.() -> Unit) {
        action.invoke(this.graph)
    }

    fun node(factory: () -> V): V {
        val node = factory.invoke()
        this.graph.addNode(node)
        return node
    }

    fun V.trigger(to: V, groupId: String? = null): V {
        accessGraph {
            if (groupId != null) {
                addTriggerEdge(this@trigger.nodeId, to.nodeId, groupId)
            } else {
                addTriggerEdge(this@trigger.nodeId, to.nodeId)
            }
        }

        return this
    }

    fun <V: AbstractGraphNode> V.transmit(
        fromParameter: String,
        to: AbstractGraphNode,
        toParameter: String
    ): V {
        accessGraph {
            addParameterTransmissionEdge(this@transmit.nodeId, to.nodeId, fromParameter, toParameter)
        }

        return this
    }

    fun build(): G {
        return this.graph
    }
}