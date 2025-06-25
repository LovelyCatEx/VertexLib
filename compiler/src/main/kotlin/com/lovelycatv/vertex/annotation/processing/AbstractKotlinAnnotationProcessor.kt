package com.lovelycatv.vertex.annotation.processing

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.lovelycatv.vertex.lang.adapter.kotlin.DefaultKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-24 04:09
 * @version 1.0
 */
abstract class AbstractKotlinAnnotationProcessor(
    protected val environment: SymbolProcessorEnvironment
) : IAnnotationProcessor, SymbolProcessor {
    override fun getAdapterContext(): IKotlinAdapterContext {
        return DefaultKotlinAdapterContext()
    }

    final override fun process(resolver: Resolver): List<KSAnnotated> {
        val adapterContext = this.getAdapterContext()
        val originalAnnotatedNodes = this.getSupportedAnnotations().associateWith {
            resolver.getSymbolsWithAnnotation(it.qualifiedName!!)
        }

        this.process(originalAnnotatedNodes.mapValues { (_, nodes) ->
            nodes.map {
                adapterContext.translateAnnotated(it)
            }.toList()
        })

        return emptyList()
    }
}