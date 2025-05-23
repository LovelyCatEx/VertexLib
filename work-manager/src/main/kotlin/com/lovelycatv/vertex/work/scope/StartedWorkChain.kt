package com.lovelycatv.vertex.work.scope

import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.worker.WorkChain
import com.lovelycatv.vertex.work.worker.WrappedWork
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.cancel

/**
 * @author lovelycat
 * @since 2024-11-01 18:45
 */
class StartedWorkChain(
    val originalWorkChain: WorkChain,
    private val chainCoroutineScope: WorkChainCoroutineScope,
    private val workCoroutineScope: WorkCoroutineScope,
    private val workChainResult: Deferred<WorkData?>
) {
    fun getChainId() = this.originalWorkChain.chainId

    fun isRunning(): Boolean {
        return !this.workCoroutineScope.isAvailable()
    }

    fun getRunningWorkers(): Set<WrappedWork> {
        return this.workCoroutineScope.getActiveJobs().keys
    }

    fun getWorkerById(id: String): WrappedWork? {
        return this.originalWorkChain.getAllWorks().find { it.getWorkerId() == id }
    }

    suspend fun stop(reason: String = "") {
        this.chainCoroutineScope.cancel(reason)
        this.workCoroutineScope.stopCurrentWorks(reason)
        println("WorkChain was stopped for reason: $reason")
    }

    fun forceStop(reason: String = "") {
        this.chainCoroutineScope.cancel(reason)
        this.workCoroutineScope.forceStopCurrentWorks(reason)
        println("WorkChain was force stopped for reason: $reason")
    }

    suspend fun await(): WorkData? {
        return this.workChainResult.await()
    }
}