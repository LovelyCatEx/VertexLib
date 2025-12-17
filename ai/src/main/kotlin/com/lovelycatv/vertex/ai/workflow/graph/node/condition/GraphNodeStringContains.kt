package com.lovelycatv.vertex.ai.workflow.graph.node.condition

import com.lovelycatv.vertex.ai.workflow.graph.node.BaseGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeType
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-12-18 02:21
 * @version 1.0
 */
class GraphNodeStringContains(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String,
    val type: Type = Type.CONTAINS
) : BaseGraphNode(
    GraphNodeType.STRING_CONTAINS,
    nodeId,
    nodeName,
    listOf(INPUT_X, INPUT_Y),
    listOf(OUTPUT_Z)
) {
    companion object {
        val INPUT_X = GraphNodeParameter(
            String::class,
            "x"
        )
        val INPUT_Y = GraphNodeParameter(
            String::class,
            "y"
        )
        val OUTPUT_Z = GraphNodeParameter(
            Boolean::class,
            "z"
        )
    }

    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        val x = super.resolveParameterReference(inputData, INPUT_X) as String
        val y = super.resolveParameterReference(inputData, INPUT_Y) as String

        return mapOf(
            INPUT_X to if (type == Type.CONTAINS)
                x.contains(y)
            else
                !x.contains(y)
        )
    }

    override fun serialize(): Map<String, Any> {
        return super.serialize() + mapOf(
            "contains" to this.type.name
        )
    }

    enum class Type {
        CONTAINS,
        NOT_CONTAINS
    }
}