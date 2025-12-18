package com.lovelycatv.vertex.ai.agent

import com.lovelycatv.vertex.workflow.graph.WorkFlowGraph
import com.lovelycatv.vertex.workflow.graph.WorkFlowGraphListener
import com.lovelycatv.vertex.workflow.graph.composer.WorkFlowGraphComposer
import com.lovelycatv.vertex.workflow.graph.node.GraphNodeParameter
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
    private val workFlowGraphComposer = WorkFlowGraphComposer<R>(
        graphFactory = {
            WorkFlowGraph(agentName)
        }
    )

    fun composeWorkFlowGraph(action: WorkFlowGraphComposer<R>.() -> Unit) {
        action.invoke(this.workFlowGraphComposer)
    }

    fun start(inputData: Map<String, Pair<KClass<*>, Any?>>, listener: WorkFlowGraphListener<R>?): String {
        return this.workFlowGraphComposer.getCurrentGraph().start(
            inputData.mapKeys {
                GraphNodeParameter(it.value.first, it.key)
            }.mapValues { it.value.second },
            listener
        )
    }

    fun stop(taskId: String) {
        this.workFlowGraphComposer.getCurrentGraph().stop(taskId)
    }
}