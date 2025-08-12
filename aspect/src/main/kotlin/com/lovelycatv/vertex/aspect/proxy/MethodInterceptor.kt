package com.lovelycatv.vertex.aspect.proxy

import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-11 01:53
 * @version 1.0
 */
abstract class MethodInterceptor {
    abstract fun intercept(target: Any, method: Method, args: Array<out Any?>, methodProxy: MethodProxy): Any?
}