package com.lovelycatv.vertex.lang.model

import com.lovelycatv.vertex.annotation.processing.SharedVertexCompilerModuleTest
import com.lovelycatv.vertex.annotation.processing.annotations.TestAnnotation
import com.lovelycatv.vertex.annotation.processing.playground.EmptyJavaClass
import com.lovelycatv.vertex.annotation.processing.playground.EverythingIncludedJavaClass
import com.lovelycatv.vertex.annotation.processing.playground.EverythingIncludedJavaClassImpl
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeVariable

class ElementUtilsKtTest {
    companion object {
        lateinit var everythingIncludedJavaClassImplElement: Element
        lateinit var everythingIncludedJavaClassElement: Element
        lateinit var emptyClassElement: Element

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            SharedVertexCompilerModuleTest.testAnnotationProcessor()

            this.everythingIncludedJavaClassImplElement = SharedVertexCompilerModuleTest.processor.elements[EverythingIncludedJavaClassImpl::class]!!
            this.everythingIncludedJavaClassElement = SharedVertexCompilerModuleTest.processor.elements[EverythingIncludedJavaClass::class]!!
            this.emptyClassElement = SharedVertexCompilerModuleTest.processor.elements[EmptyJavaClass::class]!!
        }
    }
    @Test
    fun getAnnotationOrNull() {
        val testAnnotation = everythingIncludedJavaClassImplElement.getAnnotation(TestAnnotation::class.java)
        assertTrue(testAnnotation != null, "EverythingIncludedJavaClassImpl should be annotated with @TestAnnotation")

        val nullAnnotation = everythingIncludedJavaClassImplElement.getAnnotation(Override::class.java)
        assertTrue(nullAnnotation == null, "@Override should not be found at EverythingIncludedJavaClassImpl")
    }

    @Test
    fun isClassElement() {
        assertTrue(everythingIncludedJavaClassImplElement.isClassElement(), "Element of EverythingIncludedJavaClassImpl should be a typeElement")
    }

    @Test
    fun isExecutableElement() {
        assertTrue(everythingIncludedJavaClassElement.enclosedElements.filterIsInstance<ExecutableElement>().map {
            println(it.toString())
            it.isExecutableElement()
        }.all { it })
    }

    @Test
    fun isVariableElement() {
        assertTrue(everythingIncludedJavaClassElement.enclosedElements.filterIsInstance<VariableElement>().map {
            println(it.toString())
            it.isVariableElement()
        }.all { it })
    }

    @Test
    fun isTypeParameterElement() {
        assertTrue((everythingIncludedJavaClassElement.asType() as DeclaredType).typeArguments.filterIsInstance<TypeVariable>().map {
            println("typeName: $it, lower: ${it.lowerBound}, upper: ${it.upperBound}")
            it.asElement().isTypeParameterElement()
        }.all { it })
    }
}