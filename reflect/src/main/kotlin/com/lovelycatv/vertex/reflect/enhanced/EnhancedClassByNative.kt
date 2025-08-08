package com.lovelycatv.vertex.reflect.enhanced

import com.lovelycatv.vertex.reflect.MethodSignature
import java.lang.reflect.Modifier

/**
 * @author lovelycat
 * @since 2025-08-08 15:34
 * @version 1.0
 */
abstract class EnhancedClassByNative(originalClass: Class<*>) : EnhancedClass(originalClass) {
    init {
        val qualifiedMethods = originalClass.declaredMethods.filter { !Modifier.isFinal(it.modifiers) && !it.isSynthetic }

        qualifiedMethods.forEachIndexed { index, method ->
            super.signatureToIndex[MethodSignature(method)] = index
        }
    }
}