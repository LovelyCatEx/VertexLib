package com.lovelycatv.vertex.annotation.processing

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.lovelycatv.vertex.lang.adapter.kotlin.DefaultKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.AbstractKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-24 04:09
 * @version 1.0
 */
abstract class AbstractKotlinAnnotationProcessor(
    protected val environment: SymbolProcessorEnvironment
) : IAnnotationProcessor, SymbolProcessor {
    override fun getAdapterContext(): AbstractKotlinAdapterContext {
        return DefaultKotlinAdapterContext()
    }

    final override fun process(resolver: Resolver): List<KSAnnotated> {
        val adapterContext = this.getAdapterContext()
        val originalElements = this.getSupportedAnnotations().associateWith {
            resolver.getSymbolsWithAnnotation(it.qualifiedName!!)
        }

        this.process(originalElements.mapValues { (_, elements) ->
            elements.map {
                adapterContext.translateElement(it)
            }.toList()
        })

        return emptyList()
    }
}