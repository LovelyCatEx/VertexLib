package com.lovelycatv.vertex.lang.model.type

/**
 * Represents the type of an executable. An executable is a method, constructor, or initializer.
 *
 * @author lovelycat
 * @since 2025-05-30 14:28
 * @version 1.0
 */
interface KExecutableType : KTypeMirror {
    val typeVariables: List<KTypeVariable>

    val returnType: KTypeMirror

    val parameters: List<KTypeMirror>

    val throwTypes: List<KDeclaredType>

    override fun inspect(): List<String> {
        return super.inspect() + listOf(
            if (this.typeVariables.isNotEmpty()) {
                this.typeVariables.joinToString(separator = ", ", prefix = "<", postfix = "> ") {
                    it.inspect().joinToString(separator = " ", prefix = "[", postfix = "]")
                }
            } else {
                ""
            }
            + "(${this.parameters.joinToString(separator = ", ", prefix = "", postfix = "") { it.toString() }}): ${this.returnType}"
        )
    }
}