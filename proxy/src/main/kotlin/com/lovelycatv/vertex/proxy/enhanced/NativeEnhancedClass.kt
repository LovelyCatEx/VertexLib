package com.lovelycatv.vertex.proxy.enhanced

import com.lovelycatv.vertex.reflect.MethodSignature

/**
 * @author lovelycat
 * @since 2025-08-08 15:34
 * @version 1.0
 */
abstract class NativeEnhancedClass(originalClass: Class<*>) : EnhancedClass(originalClass) {
    init {
        super.originalConstructors.forEachIndexed { index, constructor ->
            super.signatureToIndex[MethodSignature(constructor)] = index
        }

        val methodIndexOffset = super.originalConstructors.size

        super.originalMethods.forEachIndexed { index, method ->
            super.signatureToIndex[MethodSignature(method)] = index + methodIndexOffset
        }
    }
}