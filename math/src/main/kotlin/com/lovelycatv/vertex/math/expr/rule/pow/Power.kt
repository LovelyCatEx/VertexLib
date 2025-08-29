package com.lovelycatv.vertex.math.expr.rule.pow

import com.lovelycatv.vertex.math.expr.rule.primary.PrimaryExpr

/**
 * @author lovelycat
 * @since 2025-08-28 17:23
 * @version 1.0
 */
data class Power(
    val primary: PrimaryExpr,
    val power: Array<PowerExpr>
) : PowerExpr {
    val canDegradeToPrimaryExpr: Boolean get() = this.power.isEmpty()

    val degrade: PrimaryExpr get() = this.primary

    override fun internalInspect(): String {
        return primary.inspect() + power.joinToString(separator = "^", prefix = "^", postfix = "") { it.inspect() }
    }

    override fun inspectNoParens(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Power

        if (primary != other.primary) return false
        return power.contentEquals(other.power)
    }

    override fun hashCode(): Int {
        var result = primary.hashCode()
        result = 31 * result + power.contentHashCode()
        return result
    }
}