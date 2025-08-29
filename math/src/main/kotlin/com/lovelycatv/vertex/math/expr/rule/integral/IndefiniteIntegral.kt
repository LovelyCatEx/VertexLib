package com.lovelycatv.vertex.math.expr.rule.integral

import com.lovelycatv.vertex.math.expr.rule.Expression

/**
 * @author lovelycat
 * @since 2025-08-28 17:35
 * @version 1.0
 */
data class IndefiniteIntegral(val body: Expression) : IntegralExpr {
    override fun internalInspect(): String {
        return "int {${body.inspect()}}"
    }
}