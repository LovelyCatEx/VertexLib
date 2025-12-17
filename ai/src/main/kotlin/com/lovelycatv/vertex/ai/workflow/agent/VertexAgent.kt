package com.lovelycatv.vertex.ai.workflow.agent

import com.lovelycatv.vertex.ai.workflow.graph.WorkFlowGraph
import com.lovelycatv.vertex.ai.workflow.graph.WorkFlowGraphListener
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-12-17 00:55
 * @version 1.0
 */
class VertexAgent<R: Any>(
    val agentId: String,
    val agentName: String
) {
    private val workFlowGraph = WorkFlowGraph<R>(agentName)

    fun accessWorkFlowGraph(action: WorkFlowGraph<R>.() -> Unit) {
        action.invoke(this.workFlowGraph)
    }

    fun start(inputData: Map<String, Pair<KClass<*>, Any?>>, listener: WorkFlowGraphListener<R>?): String {
        return this.workFlowGraph.start(
            inputData.mapKeys {
                GraphNodeParameter(it.value.first, it.key)
            }.mapValues { it.value.second },
            listener
        )
    }

    fun stop(taskId: String) {
        this.workFlowGraph.stop(taskId)
    }
}