package com.lovelycatv.vertex.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

suspend fun <R> suspendTimeoutCoroutine(
    maxWaitTimeMillis: Long,
    checkAccuracy: Long = 10L,
    onTimeout: () -> R,
    block: (Continuation<R>) -> Unit
): R {
    val startTime = System.currentTimeMillis()
    val scope = CoroutineScope(Dispatchers.IO)

    var continuation: CancellableContinuation<R>? = null

    scope.launch {
        while (true) {
            delay(checkAccuracy)
            if (continuation?.isCompleted == true) {
                this.cancel()
                return@launch
            } else if (System.currentTimeMillis() - startTime > maxWaitTimeMillis) {
                continuation!!.cancel()
                this.cancel()
                return@launch
            }
        }
    }

    return withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine {
                continuation = it
                block.invoke(it)
            }
        } catch (e: CancellationException) {
            onTimeout.invoke()
        }
    }
}