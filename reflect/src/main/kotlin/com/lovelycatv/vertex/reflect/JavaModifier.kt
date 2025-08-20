package com.lovelycatv.vertex.reflect

/**
 * @author lovelycat
 * @since 2025-05-29 23:44
 * @version 1.0
 */
enum class JavaModifier : IJavaKeyWord {
    PUBLIC, PRIVATE, PROTECTED, FINAL,
    ABSTRACT, SEALED, STATIC, VOLATILE,
    SYNCHRONIZED, TRANSIENT, NATIVE, STRICTFP,
    NON_SEALED, INTERFACE;

    override fun getWord(): String {
        return this.name
    }

    companion object {
        fun getModifierByName(name: String): JavaModifier? {
            return entries.find { it.name.lowercase() == name.lowercase() }
        }
    }
}