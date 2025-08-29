package com.lovelycatv.vertex.math.expr.rule.basic

import com.lovelycatv.vertex.math.expr.lexer.Symbol

/**
 * @author lovelycat
 * @since 2025-08-28 17:34
 * @version 1.0
 */
data class AddSub(val mulDiv: MulDiv, val companions: Array<out Companion>) :
    BasicExpr {
    data class Companion(val symbol: Symbol, val mulDiv: MulDiv)

    override fun internalInspect(): String {
        return "${mulDiv.inspect()}${companions.flatMap { listOf(it.symbol.text, it.mulDiv.inspect()) }.joinToString(separator = "", prefix = "", postfix = "")}"
    }

    override fun inspectNoParens(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddSub

        if (mulDiv != other.mulDiv) return false
        return companions.contentEquals(other.companions)
    }

    override fun hashCode(): Int {
        var result = mulDiv.hashCode()
        result = 31 * result + companions.contentHashCode()
        return result
    }
}