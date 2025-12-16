package com.lovelycatv.vertex.ai.workflow.graph

/**
 * @author lovelycat
 * @since 2025-12-17 02:06
 * @version 1.0
 */
interface WorkFlowGraphListener {
    fun onTaskStarted(taskId: String)

    fun onTaskFinished(taskId: String, outputs: Map<String, Any?>)

    fun onTaskCancelled(taskId: String)
}