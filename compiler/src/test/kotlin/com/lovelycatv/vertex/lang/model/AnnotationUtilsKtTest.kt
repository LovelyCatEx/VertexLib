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

class AnnotationUtilsKtTest {
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

            val testAnnotation = everythingIncludedJavaClassImplElement.getAnnotation(TestAnnotation::class.java)
            assertTrue(testAnnotation != null, "EverythingIncludedJavaClassImpl should be annotated with @TestAnnotation")
        }
    }

    @Test
    fun getClassFieldValue() {
        val testAnnotationMirror = everythingIncludedJavaClassImplElement.getAnnotationMirrorOrNull(TestAnnotation::class.java)

        val expectedClasses = listOf(EmptyJavaClass::class.qualifiedName!!, Override::class.qualifiedName!!)
        val classes = testAnnotationMirror!!.getClassFieldValue("classArray", true).map { it.toString() }
        assertTrue(
            classes.containsAll(expectedClasses),
            "Value of the field classArray in @TestAnnotation of EverythingIncludedJavaClassImpl should be $expectedClasses, current: $classes"
        )

        val expectedClazz = Override::class.qualifiedName
        val clazz = testAnnotationMirror.getClassFieldValue("clazz", false).map { it.toString() }.first()
        assertTrue(
            clazz == expectedClazz,
            "Value of the field clazz in @TestAnnotation of EverythingIncludedJavaClassImpl should be $expectedClazz, current: $clazz"
        )

        assertTrue(
            testAnnotationMirror.getClassFieldValue("nullField", false).isEmpty(),
            "Field nullField in @TestAnnotation of EverythingIncludedJavaClassImpl should not be exist"
        )
    }

    @Test
    fun getFieldValue() {
        val testAnnotationMirror = everythingIncludedJavaClassImplElement.getAnnotationMirrorOrNull(TestAnnotation::class.java)

        assertTrue(
            testAnnotationMirror!!.getFieldValue<String>("name") == "EverythingIncludedJavaClassImpl",
            "Value of the field name in @TestAnnotation of EverythingIncludedJavaClassImpl should be EverythingIncludedJavaClassImpl"
        )

        assertTrue(
            testAnnotationMirror.getFieldValue<String>("name2") == null,
            "Field name2 in @TestAnnotation should not be exist"
        )
    }
}