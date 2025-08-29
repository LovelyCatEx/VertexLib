package com.lovelycatv.vertex.math.expr.rule

/**
 * @author lovelycat
 * @since 2025-08-28 17:21
 * @version 1.0
 */
interface Expression {
    fun inspect(): String {
        return if (this.inspectNoParens()) {
            this.internalInspect()
        } else {
            "(${this.internalInspect()})"
        }
    }

    fun internalInspect(): String

    fun inspectNoParens(): Boolean = true
}