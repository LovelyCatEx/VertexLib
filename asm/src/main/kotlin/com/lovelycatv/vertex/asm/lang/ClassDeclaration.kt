package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JavaModifier

/**
 * @author lovelycat
 * @since 2025-08-01 18:45
 * @version 1.0
 */
class ClassDeclaration(
    val modifiers: Array<JavaModifier>,
    val className: String,
    val superClass: TypeDeclaration? = null,
    val interfaces: Array<TypeDeclaration>? = null
) : CodeContainer() {
    private val _fields: MutableList<FieldDeclaration> = mutableListOf()
    val fields: List<FieldDeclaration> get() = this._fields

    private val _methods: MutableList<MethodDeclaration> = mutableListOf()
    val methods: List<MethodDeclaration> get() = this._methods

    val constructors: List<MethodDeclaration> get() = this.methods.filter { it.methodName == ASMUtils.CONSTRUCTOR_NAME }

    fun isNoSuperClass() = this.superClass == null || this.superClass.type == Void::class.java

    fun addField(field: FieldDeclaration) {
        this._fields.add(field)
    }

    fun addMethod(method: MethodDeclaration) {
        this._methods.add(method)
    }
}