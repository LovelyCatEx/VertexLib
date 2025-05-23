package com.lovelycatv.vertex.work.base

import com.lovelycatv.vertex.coroutines.runCoroutine
import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.data.WorkResult
import kotlinx.coroutines.*

/**
 * @author lovelycat
 * @since 2024-10-31 16:39
 * @version 1.0
 */
sealed class AbstractProtectedWork(
    workName: String,
    inputData: WorkData
) : BaseWork(workName, inputData) {
    private val protectedCoroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private val protectedJobs = mutableListOf<Job>()

    fun anyProtectJobsRunning(): Boolean {
        return protectedJobs.map { it.isActive }.toSet().run {
            this.size == 1 && this.iterator().next()
        }
    }

    private fun cancelAllProtectedJobs(reason: String) {
        protectedJobs.filter { it.isActive }.forEach {
            it.cancel(reason)
        }
    }

    fun runInProtected(fx: suspend () -> Unit) {
        val job = runCoroutine(protectedCoroutineScope) {
            fx()
        }
        this.protectedJobs.add(job)
    }

    suspend fun waitForProtectedJobs() {
        while (this.anyProtectJobsRunning()) {
            delay(100)
        }
    }

    override suspend fun startWork(preBlockOutputData: WorkData): WorkResult {
        val result = super.startWork(preBlockOutputData)
        this.waitForProtectedJobs()
        return result
    }

    override suspend fun stopWork(job: Job, reason: String) {
        this.waitForProtectedJobs()
        super.stopWork(job, reason)
    }

    override fun forceStopWork(job: Job, reason: String) {
        this.cancelAllProtectedJobs(reason)
        super.forceStopWork(job, reason)
    }
}