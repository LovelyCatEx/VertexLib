package com.lovelycatv.vertex.aspect

import com.lovelycatv.vertex.aspect.proxy.MethodProxy
import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-12 20:33
 * @version 1.0
 */
abstract class AroundAspect(order: Int) : AbstractAspect(order) {
    abstract fun around(target: Any, method: Method, args: Array<out Any?>, methodProxy: MethodProxy, procedure: AroundAspectChain)
}