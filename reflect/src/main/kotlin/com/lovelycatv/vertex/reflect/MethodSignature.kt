package com.lovelycatv.vertex.reflect

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.Objects

/**
 * @author lovelycat
 * @since 2025-08-08 10:07
 * @version 1.0
 */
class MethodSignature(
    val name: String,
    val descriptor: String
) {
    constructor(method: Method) : this(
        method.name,
        method.parameterTypes.joinToString(
            separator = ",",
            prefix = "(",
            postfix = ")"
        ) { TypeUtils.getDescriptor(it) }
    )

    constructor(constructor: Constructor<*>) : this(
        ReflectUtils.CONSTRUCTOR_NAME,
        constructor.parameterTypes.joinToString(
            separator = ",",
            prefix = "(",
            postfix = ")"
        ) { TypeUtils.getDescriptor(it) }
    )

    constructor(name: String, vararg clazz: Class<*>) : this(
        name,
        clazz.joinToString(
            separator = ",",
            prefix = "(",
            postfix = ")"
        ) { TypeUtils.getDescriptor(it) }
    )

    override fun toString(): String {
        return name + descriptor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MethodSignature) return false
        return this.name == other.name
            && this.descriptor == other.descriptor
    }

    override fun hashCode(): Int {
        return Objects.hash(name, descriptor)
    }
}