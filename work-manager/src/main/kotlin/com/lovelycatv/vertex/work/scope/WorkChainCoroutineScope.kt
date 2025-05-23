package com.lovelycatv.vertex.work.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * @author lovelycat
 * @since 2024-10-27 23:34
 * @version 1.0
 */
class WorkChainCoroutineScope : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}