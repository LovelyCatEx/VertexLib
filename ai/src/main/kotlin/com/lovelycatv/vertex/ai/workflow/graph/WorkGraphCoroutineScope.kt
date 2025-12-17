package com.lovelycatv.vertex.ai.workflow.graph

import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractGraphNode
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @author lovelycat
 * @since 2025-12-17 00:49
 * @version 1.0
 */
class WorkGraphCoroutineScope<V: AbstractGraphNode>(
    val taskId: String,
    val listener: WorkFlowGraphListener?
) : CoroutineScope {
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext =
        job + Dispatchers.IO + CoroutineName("WorkFlowGraph#${taskId}")

    private val nodeJobs = mutableMapOf<V, Job>()

    fun launchNodeExecution(node: V, fx: suspend () -> Unit) {
        val ctx = Dispatchers.IO + CoroutineName("Node#${node.nodeName}#$taskId")

        this.nodeJobs[node] = this.launch(ctx) {
            fx.invoke()
        }
    }

    fun runningNodes(): Map<V, Job> {
        return nodeJobs.filter { !it.value.isCompleted }
    }
}