package com.lovelycatv.vertex.math.expr.rule.primary

import com.lovelycatv.vertex.math.expr.rule.Expression

/**
 * @author lovelycat
 * @since 2025-08-28 17:31
 * @version 1.0
 */
data class ParensPrimary(val expr: Expression) : PrimaryExpr {
    override fun internalInspect(): String {
        return expr.inspect()
    }

    override fun inspectNoParens(): Boolean = false
}