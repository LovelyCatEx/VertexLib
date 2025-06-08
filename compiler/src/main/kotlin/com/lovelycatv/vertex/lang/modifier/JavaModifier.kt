package com.lovelycatv.vertex.lang.modifier

/**
 * @author lovelycat
 * @since 2025-05-29 23:44
 * @version 1.0
 */
enum class JavaModifier : IModifier {
    STATIC, VOLATILE, SYNCHRONIZED, TRANSIENT, NATIVE, STRICTFP, NON_SEALED;

    override fun getModifierName(): String {
        return this.name
    }

    companion object {
        fun getModifierByName(name: String): IModifier? {
            return entries.find { it.getModifierName() == name }
        }
    }
}