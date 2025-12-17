package com.lovelycatv.vertex.workflow.graph.node.math

import com.lovelycatv.vertex.number.compareTo
import com.lovelycatv.vertex.workflow.graph.node.BaseGraphNode
import com.lovelycatv.vertex.workflow.graph.node.GraphNodeParameter
import com.lovelycatv.vertex.workflow.graph.node.GraphNodeType
import java.util.*

/**
 * @author lovelycat
 * @since 2025-12-17 23:12
 * @version 1.0
 */
class GraphNodeNumberComparator(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String,
    val type: Type
) : BaseGraphNode(
    GraphNodeType.NUMBER_COMPARATOR,
    nodeId,
    nodeName,
    listOf(INPUT_X, INPUT_Y),
    listOf(OUTPUT_Z)
) {
    companion object {
        val INPUT_X = GraphNodeParameter(
            Number::class,
            "x"
        )
        val INPUT_Y = GraphNodeParameter(
            Number::class,
            "y"
        )
        val OUTPUT_Z = GraphNodeParameter(
            Boolean::class,
            "z"
        )
    }

    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        val x = resolveParameterReference(inputData, INPUT_X.name) as Number
        val y = resolveParameterReference(inputData, INPUT_Y.name) as Number

        val z = when (type) {
            Type.GT -> x > y
            Type.EQ -> x == y
            Type.LT -> x < y
            Type.GTE -> x >= y
            Type.LTE -> x <= y
        }

        return mapOf(
            OUTPUT_Z to z
        )
    }

    override fun serialize(): Map<String, Any> {
        return super.serialize() + mapOf(
            "type" to this.type.name
        )
    }

    enum class Type {
        GT,
        EQ,
        LT,
        GTE,
        LTE
    }
}