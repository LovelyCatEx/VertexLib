package com.lovelycatv.vertex.workflow.graph.node

/**
 * @author lovelycat
 * @since 2025-12-16 23:59
 * @version 1.0
 */
enum class GraphNodeType : IGraphNodeType {
    ENTRY,
    EXIT,
    IF,
    ADD,
    SUB,
    MUL,
    DIV,
    NUMBER_COMPARATOR,
    STRING_CONTAINS;

    override fun getTypeName(): String {
        return this.name
    }
}