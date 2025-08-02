package com.lovelycatv.vertex.aspect

import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-01 02:16
 * @version 1.0
 */
abstract class AbstractAspect {
    abstract fun before(target: Any, method: Method, args: Array<out Any?>)

    abstract fun invocation(target: Any, method: Method, args: Array<out Any?>): Any?

    abstract fun after(target: Any, method: Method, args: Array<out Any?>)
}