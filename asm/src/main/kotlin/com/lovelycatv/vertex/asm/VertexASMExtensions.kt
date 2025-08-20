package com.lovelycatv.vertex.asm

import com.lovelycatv.vertex.asm.lang.MethodDeclaration
import com.lovelycatv.vertex.asm.lang.ParameterDeclaration
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.CodeWriter
import com.lovelycatv.vertex.reflect.ReflectUtils
import java.lang.reflect.Method
import java.lang.reflect.Parameter

/**
 * @author lovelycat
 * @since 2025-08-20 17:50
 * @version 1.0
 */
class VertexASMExtensions private constructor()

fun Class<*>.toTypeDeclaration(): TypeDeclaration = TypeDeclaration.fromClass(this)

fun Array<Class<*>>.toTypeDeclarations(): List<TypeDeclaration> = this.map { it.toTypeDeclaration() }

fun Iterable<Class<*>>.toTypeDeclarations(): List<TypeDeclaration> = this.map { it.toTypeDeclaration() }

fun Parameter.toParameterDeclaration(): ParameterDeclaration = ParameterDeclaration.fromType(this.name, TypeDeclaration.fromClass(this.type))

fun Array<Parameter>.toParameterDeclarations(): List<ParameterDeclaration> = this.map { it.toParameterDeclaration() }

fun Iterable<Parameter>.toParameterDeclarations(): List<ParameterDeclaration> = this.map { it.toParameterDeclaration() }

fun Method.toMethodDeclaration(codeWriter: (CodeWriter.() -> Unit)? = null) = MethodDeclaration(
    modifiers = ReflectUtils.getModifiersFromMod(this.modifiers).toTypedArray(),
    methodName = this.name,
    parameters = this.parameters.toParameterDeclarations().toTypedArray(),
    returnType = TypeDeclaration.fromClass(this.returnType),
    throws = this.exceptionTypes.toTypeDeclarations().toTypedArray(),
    fxCodeWriter = codeWriter
)
