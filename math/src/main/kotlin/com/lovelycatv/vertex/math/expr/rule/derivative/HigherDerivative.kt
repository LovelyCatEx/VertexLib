package com.lovelycatv.vertex.math.expr.rule.derivative

import com.lovelycatv.vertex.math.expr.lexer.IdentifierLiteral
import com.lovelycatv.vertex.math.expr.rule.Expression

/**
 * @author lovelycat
 * @since 2025-08-28 17:37
 * @version 1.0
 */
data class HigherDerivative(val variable: IdentifierLiteral, val expr: Expression, val n: Expression) : DerivativeExpr {
    override fun internalInspect(): String {
        return "d^${n.inspect()}/d^${n.inspect()} $variable ${expr.inspect()}"
    }
}