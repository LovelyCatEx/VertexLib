package com.lovelycatv.vertex.reflect.enhanced

import com.lovelycatv.vertex.reflect.MethodSignature
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

/**
 * @author lovelycat
 * @since 2025-08-08 08:56
 * @version 1.0
 */
abstract class JavaEnhancedClass(originalClass: Class<*>) : EnhancedClass(originalClass) {
    protected val constructors: Array<MethodHandle>
    protected val methodHandles: Array<MethodHandle>

    init {
        val lookup = MethodHandles.lookup()

        constructors = super.originalConstructors.mapIndexed { index, constructor ->
            val constructorMh = lookup.unreflectConstructor(constructor)
            super.signatureToIndex[MethodSignature(constructor)] = index
            constructorMh
        }.toTypedArray()

        val methodIndexOffset = super.originalConstructors.size

        methodHandles = super.originalMethods.mapIndexed { index, method ->
            val methodHandle = lookup.unreflect(method)
            super.signatureToIndex[MethodSignature(method)] = index + methodIndexOffset
            methodHandle
        }.toTypedArray()


    }
}