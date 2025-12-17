package com.lovelycatv.vertex.ai.workflow.graph.node

import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-12-17 14:23
 * @version 1.0
 */
class GraphNodeIf(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String
) : AbstractSerializableGraphNode(
    GraphNodeType.IF,
    nodeId,
    nodeName,
    listOf(
        GraphNodeParameter(
            Boolean::class,
            INPUT_CONDITION
        )
    ),
    listOf()
) {
    companion object {
        const val INPUT_CONDITION = "condition"
        const val GROUP_PASSED = "passed"
        const val GROUP_FAILED = "failed"
    }

    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        return emptyMap()
    }

    override fun determineTriggerGroups(inputData: Map<GraphNodeParameter, Any?>): List<String> {
        val condition = super.resolveParameterReference(inputData, INPUT_CONDITION) as Boolean
        return listOf(
            if (condition) {
                GROUP_PASSED
            } else {
                GROUP_FAILED
            }
        )
    }
}