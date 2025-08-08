package com.lovelycatv.vertex.reflect.enhanced

import com.lovelycatv.vertex.reflect.MethodSignature
import com.lovelycatv.vertex.reflect.enhanced.factory.EnhancedClassByMethodHandleFactory
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Modifier

/**
 * @author lovelycat
 * @since 2025-08-08 08:56
 * @version 1.0
 */
abstract class EnhancedClassByMethodHandle(originalClass: Class<*>) : EnhancedClass(originalClass) {
    protected val methodHandles: Array<MethodHandle>

    init {
        val lookup = MethodHandles.lookup()

        val qualifiedMethods = originalClass.declaredMethods.filter { !Modifier.isFinal(it.modifiers) && !it.isSynthetic }

        methodHandles = qualifiedMethods.mapIndexed { index, method ->
            val methodHandle = lookup.unreflect(method)
            super.signatureToIndex[MethodSignature(method)] = index
            methodHandle
        }.toTypedArray()
    }
}