package com.lovelycatv.vertex.coroutines.jvm

import kotlinx.coroutines.Dispatchers
import java.util.concurrent.CountDownLatch
import java.util.function.Consumer
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

object CoroutineBridge {
    @JvmStatic
    fun createContinuation(): Continuation<Any?> {
        return createContinuation(Dispatchers.Default, null)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <R> createContinuation(resumeAction: Consumer<R>?): Continuation<Any?> {
        return createContinuation(Dispatchers.Default, resumeAction) as Continuation<Any?>
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <R> createContinuationOnMain(): Continuation<Any?> {
        return createContinuation<R>(Dispatchers.Main, null) as Continuation<Any?>
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <R> createContinuationOnMain(resumeAction: Consumer<R>?): Continuation<Any?> {
        return createContinuation(Dispatchers.Default, resumeAction) as Continuation<Any?>
    }

    @JvmStatic
    fun createContinuationOnIO(): Continuation<Any?> {
        return createContinuation(Dispatchers.IO, null)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <R> createContinuationOnIO(resumeAction: Consumer<R>?): Continuation<Any?> {
        return createContinuation(Dispatchers.Default, resumeAction) as Continuation<Any?>
    }

    @JvmStatic
    fun createContinuation(context: CoroutineContext): Continuation<Any?> {
        return createContinuation(context, null)
    }

    @JvmStatic
    fun <R> createContinuation(context: CoroutineContext, resumeAction: Consumer<R>?): Continuation<R> {
        return object : Continuation<R> {
            override val context: CoroutineContext
                get() = context

            override fun resumeWith(result: Result<R>) {
                resumeAction?.accept(result.getOrThrow())

            }
        }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <R> callSuspendFunctionAsync(context: CoroutineContext, callingFunction: ContinuationConsumer, resumeAction: Consumer<R>?) {
        val result = callingFunction.accept(createContinuation(context, resumeAction) as Continuation<Any?>)
        if (result.toString() != "COROUTINE_SUSPENDED") {
            resumeAction?.accept(result as R)
        }
    }

    @JvmStatic
    fun <R> awaitSuspendFunction(context: CoroutineContext, callingFunction: ContinuationConsumer): R {
        val cdl = CountDownLatch(1)

        var result: R? = null

        callSuspendFunctionAsync<R>(context, callingFunction) {
            result = it
            cdl.countDown()
        }

        cdl.await()

        return result ?: throw IllegalStateException("suspend function does not return a valid value.")
    }
}