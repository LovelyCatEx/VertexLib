package com.lovelycatv.vertex.lang.model

import com.lovelycatv.vertex.annotation.processing.SharedVertexCompilerModuleTest
import com.lovelycatv.vertex.annotation.processing.playground.EmptyJavaClass
import com.lovelycatv.vertex.annotation.processing.playground.EverythingIncludedJavaClass
import com.lovelycatv.vertex.annotation.processing.playground.EverythingIncludedJavaClassImpl
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import kotlin.test.assertTrue

class TypeMirrorUtilsKtTest {
    companion object {
        lateinit var everythingIncludedJavaClassImplElement: Element
        lateinit var everythingIncludedJavaClassElement: Element
        lateinit var emptyClassElement: Element

        lateinit var everythingIncludedJavaClassImplType: TypeMirror
        lateinit var everythingIncludedJavaClassType: TypeMirror
        lateinit var emptyClassType: TypeMirror

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            SharedVertexCompilerModuleTest.testAnnotationProcessor()

            this.everythingIncludedJavaClassImplElement = SharedVertexCompilerModuleTest.processor.elements[EverythingIncludedJavaClassImpl::class]!!
            this.everythingIncludedJavaClassElement = SharedVertexCompilerModuleTest.processor.elements[EverythingIncludedJavaClass::class]!!
            this.emptyClassElement = SharedVertexCompilerModuleTest.processor.elements[EmptyJavaClass::class]!!

            this.everythingIncludedJavaClassImplType = SharedVertexCompilerModuleTest.processor.types[EverythingIncludedJavaClassImpl::class]!!
            this.everythingIncludedJavaClassType = SharedVertexCompilerModuleTest.processor.types[EverythingIncludedJavaClass::class]!!
            this.emptyClassType = SharedVertexCompilerModuleTest.processor.types[EmptyJavaClass::class]!!
        }
    }

    @Test
    fun isClassType() {
        assertTrue(emptyClassType.isClassType(), "EmptyClass should be a declaredType")
    }

    @Test
    fun isPrimitiveType() {
        assertTrue(
            everythingIncludedJavaClassElement.enclosedElements
                .filterIsInstance<VariableElement>()
                .find { it.simpleName.toString() == "primitiveInt" }
                ?.asType()?.isPrimitiveType() == true,
            "primitiveInt in EverythingIncludedJavaClass should be a primitive int type"
        )
    }

    @Test
    fun isArrayType() {
        assertTrue(
            everythingIncludedJavaClassElement.enclosedElements
                .filterIsInstance<VariableElement>()
                .find { it.simpleName.toString() == "protectedStringArray" }
                ?.asType()?.isArrayType() == true,
            "protectedStringArray in EverythingIncludedJavaClass should be a array type"
        )
    }

    @Test
    fun isNullType() {
        assertTrue(
            (everythingIncludedJavaClassType as DeclaredType).typeArguments
                .filterIsInstance<TypeVariable>()
                .all { it.lowerBound.isNullType() },
            "All lowerBounds of typeParameters in EverythingIncludedJavaClass should be null"
        )
    }

    @Test
    fun isNoType() {
        assertTrue(
            everythingIncludedJavaClassElement.enclosingElement.asType().isNoType(),
            "Type of enclosingElement of EverythingIncludedJavaClass should be noType(or packageType)"
        )
    }

    @Test
    fun isVoidType() {
        assertTrue(
            everythingIncludedJavaClassElement.enclosedElements
                .filterIsInstance<ExecutableElement>()
                .find { it.simpleName.toString() == "noParameterFunctionReturnsVoid" }
                ?.returnType?.isVoidType() == true,
            "noParameterFunctionReturnsVoid() in EverythingIncludedJavaClass should returns void"
        )
    }

    @Test
    fun isPackageType() {
        assertTrue(
            everythingIncludedJavaClassElement.enclosingElement.asType().isPackageType(),
            "Type of enclosingElement of EverythingIncludedJavaClass should be packageType"
        )
    }

    @Test
    fun isTypeVariable() {
        assertTrue(
            (everythingIncludedJavaClassType as DeclaredType).typeArguments
                .filterIsInstance<TypeVariable>()
                .map { it.isTypeVariable() }
                .all { it },
            "All typeParameters in EverythingIncludedJavaClass should be typeVariable"
        )
    }

    @Test
    fun isWildcardType() {
        assertTrue(
            (everythingIncludedJavaClassElement.enclosedElements
                .filterIsInstance<ExecutableElement>()
                .find { it.simpleName.toString() == "typeParameterizedFunction" }
                ?.parameters?.get(4)?.asType() as DeclaredType).typeArguments.first().isWildcardType(),
            "TypeArgument in 4th parameter (Class<?>) of typeParameterizedFunction() in EverythingIncludedJavaClass should be wildcardType"
        )
    }
}