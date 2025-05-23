package com.lovelycatv.vertex.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * @author lovelycat
 * @since 2024-10-27 19:11
 * @version 1.0
 */
class CoroutineExtension private constructor()

@OptIn(DelicateCoroutinesApi::class)
fun runCoroutine(
    coroutineScope: CoroutineScope = GlobalScope,
    coroutineContext: CoroutineContext = Dispatchers.IO,
    fx: suspend () -> Unit
): Job {
    return coroutineScope.launch(coroutineContext) {
        fx()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <R> runCoroutineAsync(
    coroutineScope: CoroutineScope = GlobalScope,
    coroutineContext: CoroutineContext = Dispatchers.IO,
    fx: suspend () -> R
): Deferred<R> {
    return coroutineScope.async(coroutineContext) {
        fx()
    }
}