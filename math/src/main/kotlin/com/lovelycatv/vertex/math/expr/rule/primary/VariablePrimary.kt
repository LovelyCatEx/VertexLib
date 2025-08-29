package com.lovelycatv.vertex.math.expr.rule.primary

import com.lovelycatv.vertex.math.expr.lexer.IdentifierLiteral

/**
 * @author lovelycat
 * @since 2025-08-28 17:22
 * @version 1.0
 */
data class VariablePrimary(val variableName: IdentifierLiteral) : PrimaryExpr {
    override fun internalInspect(): String {
        return variableName.id
    }

    override fun inspectNoParens(): Boolean = true
}