package com.lovelycatv.vertex.math.expr.rule.unary

import com.lovelycatv.vertex.math.expr.lexer.Symbol

/**
 * @author lovelycat
 * @since 2025-08-28 17:25
 * @version 1.0
 */
data class Unary(val symbol: Symbol, val expr: UnaryExpr) : UnaryExpr {
    override fun internalInspect(): String {
        return "${symbol.text}${expr.inspect()}"
    }

    override fun inspectNoParens(): Boolean = true
}