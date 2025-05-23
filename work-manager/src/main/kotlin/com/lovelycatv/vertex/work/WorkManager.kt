package com.lovelycatv.vertex.work

import com.lovelycatv.vertex.work.base.AbstractWork
import com.lovelycatv.vertex.work.extension.WorkBuilder
import com.lovelycatv.vertex.work.interceptor.AbstractWorkChainInterceptor
import com.lovelycatv.vertex.work.scope.StartedWorkChain
import com.lovelycatv.vertex.work.worker.WorkChain
import com.lovelycatv.vertex.work.worker.WrappedWork
import kotlinx.coroutines.Dispatchers
import java.util.UUID
import kotlin.coroutines.CoroutineContext

/**
 * @author lovelycat
 * @since 2024-10-27 19:40
 * @version 1.0
 */
class WorkManager : AbstractWorkManager() {

    inline fun <reified W: AbstractWork> runSingleWork(
        coroutineContext: CoroutineContext = Dispatchers.IO,
        interceptor: AbstractWorkChainInterceptor? = null,
        workBuilder: WrappedWork.Builder<W>.() -> WrappedWork
    ): StartedWorkChain {
        val workChain = WorkChain.Builder().sequence(workBuilder.invoke(WrappedWork.Builder(UUID.randomUUID().toString(), W::class))).build()
        return this.runWorkChain(workChain, coroutineContext, interceptor)
    }

    fun runSingleWork(
        work: WrappedWork,
        coroutineContext: CoroutineContext = Dispatchers.IO,
        interceptor: AbstractWorkChainInterceptor? = null
    ): StartedWorkChain {
        val workChain = WorkChain.Builder().sequence(work).build()
        return this.runWorkChain(workChain, coroutineContext, interceptor)
    }


    fun runSequenceWorks(
        vararg work: WrappedWork,
        coroutineContext: CoroutineContext = Dispatchers.IO,
        interceptor: AbstractWorkChainInterceptor? = null
    ): StartedWorkChain {
        val workChain = WorkChain.Builder().sequence(*work).build()
        return this.runWorkChain(workChain, coroutineContext, interceptor)
    }

    fun runParallelWorks(
        vararg work: WrappedWork,
        coroutineContext: CoroutineContext = Dispatchers.IO,
        interceptor: AbstractWorkChainInterceptor? = null
    ): StartedWorkChain {
        val workChain = WorkChain.Builder().parallel(*work).build()
        return this.runWorkChain(workChain, coroutineContext, interceptor)
    }
}