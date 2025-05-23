package com.lovelycatv.vertex.work.base

import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.WorkState
import com.lovelycatv.vertex.work.exception.WorkNotCompleteException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author lovelycat
 * @since 2024-10-31 15:59
 * @version 1.0
 */
sealed class AbstractStateWork(
    workName: String,
    inputData: WorkData
) : AbstractProtectedWork(workName, inputData) {
    private var workResult = MutableStateFlow(WorkResult(WorkState.INITIALIZED))

    fun getCurrentWorkResultFlow() = this.workResult

    fun getCurrentWorkResult() = this.workResult.value

    override suspend fun startWork(preBlockOutputData: WorkData): WorkResult {
        val currentState = this.getCurrentWorkResult().state
        if (currentState != WorkState.RUNNING) {
            return try {
                postWorkResult(WorkResult(WorkState.RUNNING))
                val result = super.startWork(preBlockOutputData)
                postWorkResult(result)
                result
            } catch (e: Exception) {
                postWorkResult(WorkResult.error(e, e.message ?: e.localizedMessage ?: ""))
                getCurrentWorkResult()
                // Throw the exception to WorkCoroutineScope
                throw e
            }
        } else {
            throw WorkNotCompleteException(this)
        }
    }

    override suspend fun stopWork(job: Job, reason: String) {
        super.stopWork(job, reason)
        this.postWorkResult(WorkResult.stopped(reason))
    }

    override fun forceStopWork(job: Job, reason: String) {
        super.forceStopWork(job, reason)
        this.postWorkResult(WorkResult.stopped(reason))
    }

    private fun postWorkResult(workResult: WorkResult) {
        this.workResult.value = workResult
    }

    protected fun postWorkProgress(outputData: WorkData) {
        this.postWorkResult(WorkResult.running(outputData))
    }
}