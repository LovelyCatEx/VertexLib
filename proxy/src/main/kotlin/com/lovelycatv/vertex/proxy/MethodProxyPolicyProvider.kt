package com.lovelycatv.vertex.proxy

import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-11 03:13
 * @version 1.0
 */
fun interface MethodProxyPolicyProvider {
    fun getPolicy(method: Method): MethodProxyPolicy
}