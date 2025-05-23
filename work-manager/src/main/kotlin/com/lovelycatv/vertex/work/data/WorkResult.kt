package com.lovelycatv.vertex.work.data

import com.lovelycatv.vertex.work.WorkState

/**
 * @author lovelycat
 * @since 2024-10-27 20:24
 * @version 1.0
 */
data class WorkResult(
    val state: WorkState = WorkState.INITIALIZED,
    val output: WorkData = WorkData.build(),
    val failedReason: String? = null,
    val stoppedReason: String? = null,
    val errorMessage: String? = null,
    val exception: Exception? = null
) {
    fun isCompletedOrStopped() = this.state == WorkState.COMPLETED || this.state == WorkState.STOPPED

    companion object {
        fun running(output: WorkData = WorkData.build()): WorkResult {
            return WorkResult(WorkState.RUNNING, output)
        }

        fun stopped(reason: String, output: WorkData = WorkData.build()): WorkResult {
            return WorkResult(WorkState.STOPPED, output, stoppedReason = reason)
        }

        fun completed(output: WorkData = WorkData.build()): WorkResult {
            return WorkResult(WorkState.COMPLETED, output)
        }

        fun failed(reason: String, output: WorkData = WorkData.build()): WorkResult {
            return WorkResult(WorkState.FAILED, output, failedReason = reason)
        }

        fun error(e: Exception, errorMessage: String = e.message ?: "", output: WorkData = WorkData.build()): WorkResult {
            return WorkResult(WorkState.ERROR, output, exception = e, errorMessage = errorMessage)
        }
    }


}