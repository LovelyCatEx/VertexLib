package com.lovelycatv.vertex.work.scope

import com.lovelycatv.vertex.coroutines.runCoroutine
import com.lovelycatv.vertex.coroutines.runCoroutineAsync
import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.worker.WrappedWork
import com.lovelycatv.vertex.work.exception.WorkCoroutineScopeAwaitTimeoutException
import com.lovelycatv.vertex.work.exception.WorkCoroutineScopeNotInitializedException
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

typealias WorkExceptionHandler = (worker: WrappedWork, e: Exception) -> Unit


class WorkCoroutineScope(
    private val context: CoroutineContext = Dispatchers.IO,
    var exceptionHandler: WorkExceptionHandler? = null
) : CoroutineScope {
    private val job = Job()

    private val startedJobs = mutableMapOf<WrappedWork, Job>()

    override val coroutineContext: CoroutineContext
        get() = context + job

    private var expectedJobs = 0

    private fun checkAvailability() {
        if (this.expectedJobs == 0) {
            throw WorkCoroutineScopeNotInitializedException()
        }
    }

    fun launchTask(wrappedWork: WrappedWork, inputData: WorkData, context: CoroutineContext = EmptyCoroutineContext): Job {
        this.checkAvailability()
        val newJob = runCoroutine(this + CoroutineName(wrappedWork.getWork().workName), context) {
            try {
                wrappedWork.getWork().startWork(inputData)
            } catch (e: Exception) {
                e.printStackTrace()
                this.exceptionHandler?.invoke(wrappedWork, e)
            }
        }
        startedJobs[wrappedWork] = newJob
        println("Worker [${wrappedWork.getWork().workName}::${wrappedWork.getWorkId()}] started")
        return newJob
    }

    fun launchTaskAsync(wrappedWork: WrappedWork, inputData: WorkData, context: CoroutineContext = EmptyCoroutineContext): Deferred<WorkResult> {
        this.checkAvailability()
        val newJob = runCoroutineAsync(this + CoroutineName(wrappedWork.getWork().workName), context) {
            try {
                wrappedWork.getWork().startWork(inputData)
            } catch (e: Exception) {
                this.exceptionHandler?.invoke(wrappedWork, e)
                wrappedWork.getWork().getCurrentWorkResult()
            }
        }
        startedJobs[wrappedWork] = newJob
        println("Worker [${wrappedWork.getWork().workName}::${wrappedWork.getWorkId()}] started")
        return newJob
    }

    suspend fun stopCurrentWorks(reason: String = "") {
        getActiveJobs().forEach { (work, workMainJob) ->
            work.getWork().stopWork(workMainJob, reason)
        }
    }

    fun forceStopCurrentWorks(reason: String = "") {
        getActiveJobs().forEach { (work, workMainJob) ->
            work.getWork().forceStopWork(workMainJob, reason)
        }
    }

    private fun getStartedJobsMap() = this.startedJobs

    fun getActiveJobs() = this.getStartedJobsMap().filter { it.value.isActive || it.key.getWork().anyProtectJobsRunning() }

    fun getInactiveJobs() = this.getStartedJobsMap().filter { !it.value.isActive && !it.key.getWork().anyProtectJobsRunning() }

    fun initialize(expectedJobs: Int) {
        this.startedJobs.clear()
        this.expectedJobs = expectedJobs
    }

    fun isAvailable(): Boolean {
        return this.getInactiveJobs().size == this.expectedJobs
    }

    suspend fun await(timeout: Long = 0) {
        val startTime = System.currentTimeMillis()
        while (!this.isAvailable()) {
            if (timeout > 0) {
                if (System.currentTimeMillis() - startTime >= timeout) {
                    throw WorkCoroutineScopeAwaitTimeoutException(this, timeout)
                }
            }
            delay(100)
        }
    }
}