package com.lovelycatv.vertex.aspect

import com.lovelycatv.vertex.proxy.MethodProxy
import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-12 21:01
 * @version 1.0
 */
class AroundAspectChain(
    val target: Any,
    val method: Method,
    val args: Array<out Any?>,
    val methodProxy: MethodProxy,
    val aroundAspects: List<AroundAspect>
) {
    private var counter = -1
    var currentResult: Any? = null
        private set

    fun nextAspect() {
        nextAspect(this.currentResult)
    }

    fun nextAspect(newResult: Any?) {
        currentResult = newResult
        if (this.counter == aroundAspects.size - 1) {
            return
        }

        this.counter++
        this.aroundAspects[counter].around(target, method, args, methodProxy, this)
    }
}