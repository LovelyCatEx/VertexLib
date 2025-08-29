package com.lovelycatv.vertex.math.expr.rule.derivative

import com.lovelycatv.vertex.math.expr.lexer.IdentifierLiteral
import com.lovelycatv.vertex.math.expr.rule.Expression

/**
 * @author lovelycat
 * @since 2025-08-28 17:37
 * @version 1.0
 */
data class NormalDerivative(val variable: IdentifierLiteral, val expr: Expression) : DerivativeExpr {
    override fun internalInspect(): String {
        return "d/d ${variable.id} ${expr.inspect()}"
    }
}