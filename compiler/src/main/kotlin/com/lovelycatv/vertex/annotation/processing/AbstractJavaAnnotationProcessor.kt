package com.lovelycatv.vertex.annotation.processing

import com.lovelycatv.vertex.lang.adapter.java.DefaultJavaAdapterContext
import com.lovelycatv.vertex.lang.util.AbstractJavaAdapterContext
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * @author lovelycat
 * @since 2025-05-28 13:18
 * @version 1.0
 */
abstract class AbstractJavaAnnotationProcessor : IAnnotationProcessor, AbstractProcessor() {
    protected lateinit var procEnv: ProcessingEnvironment

    override fun getAdapterContext(): AbstractJavaAdapterContext {
        return DefaultJavaAdapterContext()
    }

    abstract fun initialize(procEnv: ProcessingEnvironment)

    final override fun init(processingEnv: ProcessingEnvironment) {
        this.procEnv = processingEnv
        this.initialize(processingEnv)
    }

    final override fun getSupportedAnnotationTypes(): Set<String> {
        return this.getSupportedAnnotations().mapNotNull { it.qualifiedName }.toSet()
    }

    final override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val adapterContext = this.getAdapterContext()
        val originalElements = this.getSupportedAnnotations().associateWith { roundEnv.getElementsAnnotatedWith(it.java) }

        return !this.process(originalElements.mapValues {
            (_, elements) -> elements.map {
                adapterContext.translateElement(it)
            }
        })
    }
}