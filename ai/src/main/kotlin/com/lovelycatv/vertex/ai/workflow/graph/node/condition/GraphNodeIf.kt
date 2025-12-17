package com.lovelycatv.vertex.ai.workflow.graph.node.condition

import com.lovelycatv.vertex.ai.workflow.graph.node.BaseGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeType
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-12-17 14:23
 * @version 1.0
 */
class GraphNodeIf(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String
) : BaseGraphNode(
    GraphNodeType.IF,
    nodeId,
    nodeName,
    listOf(INPUT_CONDITION),
    listOf()
) {
    companion object {
        val INPUT_CONDITION = GraphNodeParameter(
            Boolean::class,
            "condition"
        )

        const val GROUP_PASSED = "passed"
        const val GROUP_FAILED = "failed"
    }

    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        return emptyMap()
    }

    override fun determineTriggerGroups(inputData: Map<GraphNodeParameter, Any?>): List<String> {
        val condition = super.resolveParameterReference(inputData, INPUT_CONDITION.name) as Boolean
        return listOf(
            if (condition) {
                GROUP_PASSED
            } else {
                GROUP_FAILED
            }
        )
    }
}