package com.lovelycatv.vertex.math.expr.rule.primary

import com.lovelycatv.vertex.math.expr.lexer.IdentifierLiteral
import com.lovelycatv.vertex.math.expr.rule.Expression

/**
 * @author lovelycat
 * @since 2025-08-28 17:20
 * @version 1.0
 */
data class FunctionCallPrimary(val functionName: IdentifierLiteral, val parameters: Array<out Expression>) : PrimaryExpr {
    override fun internalInspect(): String {
        return "${functionName}${parameters.joinToString(separator = ", ", prefix = "(", postfix = ")")}"
    }

    override fun inspectNoParens(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FunctionCallPrimary

        if (functionName != other.functionName) return false
        return parameters.contentEquals(other.parameters)
    }

    override fun hashCode(): Int {
        var result = functionName.hashCode()
        result = 31 * result + parameters.contentHashCode()
        return result
    }
}