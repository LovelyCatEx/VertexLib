package com.lovelycatv.vertex.aspect

import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-12 20:33
 * @version 1.0
 */
abstract class BeforeAspect(order: Int) : AbstractAspect(order) {
    abstract fun before(target: Any, method: Method, args: Array<out Any?>)
}