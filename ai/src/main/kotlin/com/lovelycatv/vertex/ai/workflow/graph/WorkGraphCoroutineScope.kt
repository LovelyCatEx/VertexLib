package com.lovelycatv.vertex.ai.workflow.graph

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * @author lovelycat
 * @since 2025-12-17 00:49
 * @version 1.0
 */
class WorkGraphCoroutineScope(
    val taskId: String,
    val listener: WorkFlowGraphListener?
) : CoroutineScope {
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext =
        job + Dispatchers.IO + CoroutineName("WorkFlowGraph#${taskId}")
}