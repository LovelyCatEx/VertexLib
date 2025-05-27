package com.lovelycatv.vertex.annotation.processing

import com.lovelycatv.vertex.annotation.processing.playground.EmptyJavaClass
import com.lovelycatv.vertex.annotation.processing.playground.EverythingIncludedJavaClass
import com.lovelycatv.vertex.annotation.processing.playground.EverythingIncludedJavaClassImpl
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-05-26 23:19
 * @version 1.0
 */
@SupportedAnnotationTypes("com.lovelycatv.vertex.annotation.processing.annotations.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class TestJavaAnnotationProcessor : AbstractProcessor() {
    private lateinit var processingEnvironment: ProcessingEnvironment

    private val _types: MutableMap<KClass<*>, TypeMirror> = mutableMapOf()
    val types: Map<KClass<*>, TypeMirror> get() = this._types

    private val _elements: MutableMap<KClass<*>, Element> = mutableMapOf()
    val elements: Map<KClass<*>, Element> get() = this._elements

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        this.processingEnvironment = processingEnv
        this.initForUnitTests()
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        return true
    }

    private fun initForUnitTests() {
        _elements[EverythingIncludedJavaClass::class] = this.processingEnvironment.elementUtils.getTypeElement(
            EverythingIncludedJavaClass::class.qualifiedName)
        _types[EverythingIncludedJavaClass::class] = _elements[EverythingIncludedJavaClass::class]!!.asType()

        _elements[EverythingIncludedJavaClassImpl::class] = this.processingEnvironment.elementUtils.getTypeElement(
            EverythingIncludedJavaClassImpl::class.qualifiedName)
        _types[EverythingIncludedJavaClassImpl::class] = _elements[EverythingIncludedJavaClassImpl::class]!!.asType()

        _elements[EmptyJavaClass::class] = this.processingEnvironment.elementUtils.getTypeElement(
            EmptyJavaClass::class.qualifiedName)
        _types[EmptyJavaClass::class] = _elements[EmptyJavaClass::class]!!.asType()
    }
}