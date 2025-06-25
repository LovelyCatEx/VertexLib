package com.lovelycatv.vertex.lang.model.element

import com.lovelycatv.vertex.collection.divide
import com.lovelycatv.vertex.lang.model.type.KDeclaredType

/**
 * Represents a class or interface program element.
 * Provides access to information about the class or interface and its members.
 * Note that an enum class and a record class are specialized kinds of classes and an annotation interface is a specialized kind of interface.
 *
 * @author lovelycat
 * @since 2025-05-29 23:10
 * @version 1.0
 */
interface KDeclaredTypeElement : KElement<KDeclaredType>, KElementContainer, KParameterizableElement {
    val superClass: KDeclaredType

    val interfaces: Sequence<KDeclaredType>

    val declaredVariables get() = this.declarations.filterIsInstance<KVariableElement<*>>()

    val declaredFunctions get() = this.declarations.filterIsInstance<KExecutableElement>()

    override fun inspect(): List<String> {
        return super.inspect() + listOf(
            // Modifiers
            if (this.modifiers.toList().isNotEmpty()) {
                this.modifiers.joinToString(separator = " ", prefix = "", postfix = "").lowercase() + " "
            } else {
                ""
            }
                // ClassName
                + (this.qualifiedName ?: this.simpleName)
                // Superclass
                + " : " + this.superClass.toString()
                // Interfaces
                + if (this.interfaces.toList().isNotEmpty()) {
                    ", " + this.interfaces.map { it.toString() }.joinToString(separator = ", ", prefix = "", postfix = "")
                } else {
                    ""
                }
                + " "
                // PackageName
                + "(${this.packageName})"
        ) + (listOf("Type Parameters:") + this.typeParameters.flatMap { kElement -> kElement.inspect().mapIndexed { index, it -> if (index == 0) "  > $it" else "    $it" } }.run { if (this.toList().isEmpty()) listOf("  - No Type Parameter -") else this }
            + listOf("Variables:") + this.declaredVariables.flatMap { kElement -> kElement.inspect().mapIndexed { index, it -> if (index == 0) "  > $it" else "    $it" } }.run { if (this.toList().isEmpty()) sequenceOf("  - No Variable -") else this }
            + this.declaredFunctions.toList().divide { it.simpleName != "<init>" }.run {
                val constructors = this.first
                val functions = this.second
                listOf("Constructors:") + constructors.flatMap { it.inspect() }.mapIndexed { index, it -> if (index == 0) "  > $it" else "    $it" }.run { if (this.toList().isEmpty()) listOf("  - No Constructor -") else this } +
                listOf("Functions:") + functions.flatMap { it.inspect() }.mapIndexed { index, it -> if (index == 0) "  > $it" else "    $it" }.run { if (this.toList().isEmpty()) listOf("  - No Function -") else this }
            }).map { "  $it" }
    }
}