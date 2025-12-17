package com.lovelycatv.vertex.ai.agent.graph

import com.lovelycatv.vertex.workflow.graph.node.IGraphNodeType

/**
 * @author lovelycat
 * @since 2025-12-17 00:57
 * @version 1.0
 */
enum class VertexAgentGraphNodeType : IGraphNodeType {
    LLM;

    override fun getTypeName(): String {
        return this.name
    }
}