package com.lovelycatv.vertex.ai.workflow.graph.node

/**
 * @author lovelycat
 * @since 2025-12-16 23:59
 * @version 1.0
 */
enum class GraphNodeType : IGraphNodeType {
    ENTRY,
    EXIT,
    IF;

    override fun getTypeName(): String {
        return this.name
    }
}