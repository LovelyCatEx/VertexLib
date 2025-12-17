package com.lovelycatv.vertex.workflow.graph.composer

import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-12-17 23:34
 * @version 1.0
 */
class GraphNodeInputParameterComposer : GraphNodeParameterComposer() {
    fun addInputParameter(type: KClass<*>, name: String): GraphNodeParameterComposer {
        super.addParameter(type, name)
        return this
    }
}