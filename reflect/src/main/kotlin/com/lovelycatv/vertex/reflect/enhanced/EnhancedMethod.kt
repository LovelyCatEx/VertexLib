package com.lovelycatv.vertex.reflect.enhanced

/**
 * @author lovelycat
 * @since 2025-08-08 17:06
 * @version 1.0
 */
class EnhancedMethod(
    private val enhancedClass: EnhancedClass,
    val index: Int
) {
    fun invokeMethod(target: Any, vararg args: Any?): Any? {
        return this.enhancedClass.invokeMethod(target, this.index, *args)
    }
}