package com.lovelycatv.vertex.annotation.processing

import com.lovelycatv.vertex.annotation.processing.annotations.TestAnnotation
import com.lovelycatv.vertex.annotation.processing.playground.EmptyJavaClass
import com.lovelycatv.vertex.annotation.processing.playground.EverythingIncludedJavaClass
import com.lovelycatv.vertex.annotation.processing.playground.EverythingIncludedJavaClassImpl
import com.lovelycatv.vertex.lang.adapter.java.DefaultJavaAdapterContext
import com.lovelycatv.vertex.lang.model.element.KElement
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-05-26 23:19
 * @version 1.0
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class TestJavaAnnotationProcessor : AbstractJavaAnnotationProcessor() {
    private val _types: MutableMap<KClass<*>, TypeMirror> = mutableMapOf()
    val types: Map<KClass<*>, TypeMirror> get() = this._types

    private val _elements: MutableMap<KClass<*>, Element> = mutableMapOf()
    val elements: Map<KClass<*>, Element> get() = this._elements

    override fun getSupportedAnnotations(): Set<KClass<out Annotation>> {
        return setOf(TestAnnotation::class)
    }

    override fun initialize(procEnv: ProcessingEnvironment) {
        initForUnitTests()
    }

    override fun process(map: Map<KClass<out Annotation>, List<KElement<*>>>): Boolean {
        map.forEach { (anno, elements) ->
            println("Annotation: ${anno.qualifiedName}")
            println("As Element:")
            elements.forEach {
                it.inspect().forEach {
                    println(it)
                }
            }
            println("As Type:")
            elements.map { it.asType() }.forEach {
                it.inspect().forEach {
                    println(it)
                }
            }
        }

        return true
    }

    private fun initForUnitTests() {
        _elements[EverythingIncludedJavaClass::class] = super.procEnv.elementUtils.getTypeElement(
            EverythingIncludedJavaClass::class.qualifiedName)
        _types[EverythingIncludedJavaClass::class] = _elements[EverythingIncludedJavaClass::class]!!.asType()

        _elements[EverythingIncludedJavaClassImpl::class] = super.procEnv.elementUtils.getTypeElement(
            EverythingIncludedJavaClassImpl::class.qualifiedName)
        _types[EverythingIncludedJavaClassImpl::class] = _elements[EverythingIncludedJavaClassImpl::class]!!.asType()

        _elements[EmptyJavaClass::class] = super.procEnv.elementUtils.getTypeElement(
            EmptyJavaClass::class.qualifiedName)
        _types[EmptyJavaClass::class] = _elements[EmptyJavaClass::class]!!.asType()
    }
}