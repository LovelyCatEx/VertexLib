package com.lovelycatv.vertex.aspect

import org.aspectj.weaver.tools.PointcutParser
import org.aspectj.weaver.tools.PointcutPrimitive

/**
 * @author lovelycat
 * @since 2025-08-12 20:20
 * @version 1.0
 */
class PointCut(
    val expression: String,
    aspects: Collection<AbstractAspect>
) {
    constructor(expression: String) : this(expression, listOf())

    private val _aspects: MutableList<AbstractAspect> = mutableListOf(*aspects.toTypedArray())
    val aspects: List<AbstractAspect> get() = this._aspects

    private val expr = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
        setOf(PointcutPrimitive.EXECUTION),
        this::class.java.classLoader
    ).parsePointcutExpression(expression)

    fun addAspect(aspect: AbstractAspect) {
        this._aspects.add(aspect)
    }
}