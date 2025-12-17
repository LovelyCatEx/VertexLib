package com.lovelycatv.vertex.workflow.graph.composer

import com.lovelycatv.vertex.workflow.graph.node.GraphNodeParameter
import kotlin.reflect.KClass

sealed class GraphNodeParameterComposer {
    private val parameters = mutableListOf<GraphNodeParameter>()

    fun addParameter(type: KClass<*>, name: String): GraphNodeParameterComposer {
        this.parameters.add(
            GraphNodeParameter(type, name)
        )
        return this
    }

    fun build(): List<GraphNodeParameter> {
        return this.parameters
    }
}