package com.lovelycatv.vertex.aspect

import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-13 04:04
 * @version 1.0
 */
abstract class AfterThrowingAspect(order: Int) : AbstractAspect(order) {
    abstract fun afterThrowing(target: Any, method: Method, args: Array<out Any?>, throwable: Throwable)
}