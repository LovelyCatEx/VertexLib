package com.lovelycatv.vertex.math.expr.rule.primary

import com.lovelycatv.vertex.math.expr.lexer.NumberLiteral

/**
 * @author lovelycat
 * @since 2025-08-29 17:31
 * @version 1.0
 */
class NumberPrimary(number: Number, infinite: Boolean) : NumberLiteral(number, infinite), PrimaryExpr {
    override fun internalInspect(): String {
        return if (infinite) "INF" else number.toString()
    }

    override fun inspectNoParens(): Boolean = true
}