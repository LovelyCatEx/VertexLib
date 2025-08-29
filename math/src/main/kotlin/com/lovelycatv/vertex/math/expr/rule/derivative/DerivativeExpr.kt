package com.lovelycatv.vertex.math.expr.rule.derivative

import com.lovelycatv.vertex.math.expr.rule.Expression

/**
 * @author lovelycat
 * @since 2025-08-28 17:37
 * @version 1.0
 */
interface DerivativeExpr : Expression {
    override fun inspectNoParens(): Boolean = false
}