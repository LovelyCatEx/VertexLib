package com.lovelycatv.vertex.math.expr.rule.basic

import com.lovelycatv.vertex.math.expr.lexer.Symbol
import com.lovelycatv.vertex.math.expr.rule.unary.UnaryExpr

/**
 * @author lovelycat
 * @since 2025-08-28 17:32
 * @version 1.0
 */
data class MulDiv(val unaryExpr: UnaryExpr, val companions: Array<out Companion>) : BasicExpr {
    data class Companion(val symbol: Symbol, val expr: UnaryExpr)

    override fun internalInspect(): String {
        return "${unaryExpr.inspect()}${companions.flatMap { listOf(it.symbol.text, it.expr.inspect()) }.joinToString(separator = "", prefix = "", postfix = "")}"
    }

    override fun inspectNoParens(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MulDiv

        if (unaryExpr != other.unaryExpr) return false
        return companions.contentEquals(other.companions)
    }

    override fun hashCode(): Int {
        var result = unaryExpr.hashCode()
        result = 31 * result + companions.contentHashCode()
        return result
    }
}