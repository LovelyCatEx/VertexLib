package com.lovelycatv.vertex.lang.modifier

/**
 * @author lovelycat
 * @since 2025-05-29 23:47
 * @version 1.0
 */
enum class SharedModifier : IModifier {
    PUBLIC, PRIVATE, PROTECTED, FINAL;

    override fun getModifierName(): String {
        return this.name
    }

    companion object {
        fun getModifierByName(name: String): IModifier? {
            return entries.find { it.getModifierName() == name }
        }
    }
}