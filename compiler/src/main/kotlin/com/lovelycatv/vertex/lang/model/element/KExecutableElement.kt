package com.lovelycatv.vertex.lang.model.element

import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KExecutableType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.model.type.KTypeVariable

/**
 * Represents a method, constructor, or initializer (static or instance) of a class or interface, including annotation type elements.
 *
 * @author lovelycat
 * @since 2025-05-30 14:28
 * @version 1.0
 */
interface KExecutableElement : KElement<KExecutableType>, KParameterizableElement {
    val returnType: KTypeMirror

    val parameters: List<KVariableElement<*>>

    val throwTypes: List<KDeclaredType>

    override fun inspect() = super.inspect() + listOf(
        this.modifiers.joinToString(separator = " ", prefix = "", postfix = "").lowercase()
            // TypeVariables
            + if (this.typeParameters.isNotEmpty()) {
                " " + this.typeParameters.joinToString(separator = ", ", prefix = "<", postfix = ">") { variable ->
                    variable.upperBounds.map { it.toString() }.joinToString(separator = " & ", prefix = "[", postfix = "]")
                }
            } else {
                ""
            }
            + " "
            // FunctionName
            + this.simpleName
            // Parameters
            + "(${this.parameters.map { it.asType() }.joinToString(separator = ", ", prefix = "", postfix = "")}): ${this.returnType}"
    )
}