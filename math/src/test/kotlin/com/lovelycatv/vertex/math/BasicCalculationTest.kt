package com.lovelycatv.vertex.math

import com.lovelycatv.vertex.math.expr.MathExprVisitor
import com.lovelycatv.vertex.math.expr.MathExpressionLexer
import com.lovelycatv.vertex.math.expr.MathExpressionParser
import com.lovelycatv.vertex.math.expr.rule.Expression
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author lovelycat
 * @since 2025-08-28 17:15
 * @version 1.0
 */
class BasicCalculationTest {
    private fun parseExpr(input: String): Expression {
        val charStream = CharStreams.fromString(input)
        val lexer = MathExpressionLexer(charStream)
        val tokens = CommonTokenStream(lexer)
        val parser = MathExpressionParser(tokens)

        val visitor = MathExprVisitor()
        return visitor.visitProg(parser.prog())
    }

    @Test
    fun testBasic() {
        val expr = "2^5^(-2) + 1 + 3 * (1 + 4 * (5 + 7) / (2 + 8 / (2 * 2)))"
        val parsedExpr = this.parseExpr(expr)

        assertEquals("2.0^5.0^(-2.0)+1.0+3.0*(1.0+4.0*(5.0+7.0)/(2.0+8.0/(2.0*2.0)))", parsedExpr.inspect())
    }

    @Test
    fun testIntegral() {
        val expr = "int 0^INF {-1/3 * x^3 + 2 * x^2 + a * x * (d / d x x)}"
        val parsedExpr = this.parseExpr(expr)

        assertEquals("int 0.0^INF {-1.0/3.0*x^3.0+2.0*x^2.0+a*x*((d/d x x))}", parsedExpr.inspect())
    }
}