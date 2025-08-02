package com.lovelycatv.vertex.aspect

import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-01 02:20
 * @version 1.0
 */
open class SimpleAspect : AbstractAspect() {
    override fun before(target: Any, method: Method, args: Array<out Any?>) {}

    override fun invocation(target: Any, method: Method, args: Array<out Any?>): Any? {
        return if (args.isEmpty())
            method.invoke(target)
        else
            method.invoke(target, *args)
    }

    override fun after(target: Any, method: Method, args: Array<out Any?>) {}
}