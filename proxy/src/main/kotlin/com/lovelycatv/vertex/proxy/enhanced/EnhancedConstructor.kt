package com.lovelycatv.vertex.proxy.enhanced

/**
 * @author lovelycat
 * @since 2025-08-10 16:46
 * @version 1.0
 */
class EnhancedConstructor(
    private val enhancedClass: EnhancedClass,
    val index: Int
) {
    fun invokeMethod(vararg args: Any?): Any? {
        return this.enhancedClass.invokeMethod(null, this.index, *args)
    }
}