package com.lovelycatv.vertex.lang.modifier

/**
 * @author lovelycat
 * @since 2025-05-29 23:44
 * @version 1.0
 */
enum class KotlinModifier : IModifier {
    VAL, VAR, VARARG, CROSS_INLINE, NO_INLINE, DATA, REIFIED, IN, OUT, WHERE, SUSPEND, INLINE, INFIX;

    override fun getModifierName(): String {
        return this.name
    }

    companion object {
        fun getModifierByName(name: String): IModifier? {
            return entries.find { it.getModifierName() == name }
        }
    }
}