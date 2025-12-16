package com.lovelycatv.vertex.ai.workflow.graph.node

import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-12-17 00:00
 * @version 1.0
 */
data class GraphNodeParameter(
    val type: KClass<*>,
    val name: String,
    val value: Any? = null
)