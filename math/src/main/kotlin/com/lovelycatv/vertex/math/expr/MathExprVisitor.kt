package com.lovelycatv.vertex.math.expr

import com.lovelycatv.vertex.math.expr.MathExpressionParser.*
import com.lovelycatv.vertex.math.expr.lexer.IdentifierLiteral
import com.lovelycatv.vertex.math.expr.lexer.Symbol
import com.lovelycatv.vertex.math.expr.rule.Expression
import com.lovelycatv.vertex.math.expr.rule.basic.MulDiv
import com.lovelycatv.vertex.math.expr.rule.derivative.HigherDerivative
import com.lovelycatv.vertex.math.expr.rule.derivative.NormalDerivative
import com.lovelycatv.vertex.math.expr.rule.integral.DefiniteIntegral
import com.lovelycatv.vertex.math.expr.rule.integral.IndefiniteIntegral
import com.lovelycatv.vertex.math.expr.rule.pow.Power
import com.lovelycatv.vertex.math.expr.rule.pow.PowerExpr
import com.lovelycatv.vertex.math.expr.rule.primary.*
import com.lovelycatv.vertex.math.expr.rule.unary.Unary
import com.lovelycatv.vertex.math.expr.rule.unary.UnaryExpr

/**
 * @author lovelycat
 * @since 2025-08-28 17:17
 * @version 1.0
 */
class MathExprVisitor : MathExpressionBaseVisitor<Expression>() {
    override fun visitProg(ctx: MathExpressionParser.ProgContext): Expression {
        return this.iVisitExpr(ctx.expr())
    }

    private fun iVisitExpr(ctx: MathExpressionParser.ExprContext): Expression {
        return when (ctx) {
            is AddExpressionContext -> {
                this.visitAddExpression(ctx)
            }

            is IntegralExpressionContext -> {
                this.visitIntegralExpression(ctx)
            }

            is DerivativeExpressionContext -> {
                this.visitDerivativeExpression(ctx)
            }

             else -> throw IllegalStateException("")
        }
    }

    override fun visitAddExpression(ctx: AddExpressionContext): com.lovelycatv.vertex.math.expr.rule.basic.AddSub {
        val expr = ctx.addExpr()
        val left = expr.mulExpr()[0]
        val children = expr.children.drop(1)

        return com.lovelycatv.vertex.math.expr.rule.basic.AddSub(
            mulDiv = this.visitMulExpr(left),
            companions = children.chunked(2) {
                com.lovelycatv.vertex.math.expr.rule.basic.AddSub.Companion(
                    symbol = when (it[0].text) {
                        "+" -> Symbol.PLUS
                        "-" -> Symbol.SUBTRACT
                        else -> throw IllegalArgumentException("Only + or - are supported.")
                    },
                    mulDiv = this.visitMulExpr(it[1] as MathExpressionParser.MulExprContext)
                )
            }.toTypedArray()
        )
    }

    override fun visitMulExpr(ctx: MathExpressionParser.MulExprContext): MulDiv {
        val exprList = ctx.children
        val left = exprList[0] as UnaryExprContext
        val children = exprList.drop(1)

        return MulDiv(
            unaryExpr = this.iVisitUnaryExpression(left),
            companions = children.chunked(2) {
                MulDiv.Companion(
                    symbol = when (it[0].text) {
                        "*" -> Symbol.MULTIPLY
                        "/" -> Symbol.DIVIDE
                        else -> throw IllegalArgumentException("Only * or / are supported. Input: ${it[0].text}")
                    },
                    expr = this.iVisitUnaryExpression(it[1] as UnaryExprContext)
                )
            }.toTypedArray()
        )
    }

    private fun iVisitUnaryExpression(ctx: UnaryExprContext): UnaryExpr {
        return when (ctx) {
            is UnaryExpressionContext -> {
                val symbol = when (val symbolText = ctx.children[0].text) {
                    "+" -> Symbol.PLUS
                    "-" -> Symbol.SUBTRACT
                    else -> throw IllegalArgumentException("Only + or - are supported. Input: $symbolText")
                }

                Unary(symbol = symbol, expr = this.iVisitUnaryExpression(ctx.children[1] as UnaryExprContext))
            }

            is DegradedUnaryContext -> {
                this.iVisitPowExpression(ctx.powExpr())
            }

            else -> throw IllegalArgumentException("Unsupported context: ${ctx::class.java.canonicalName}")
        }
    }

    private fun iVisitPowExpression(ctx: PowExprContext): PowerExpr {
        return when (ctx) {
            is PowExpressionContext -> {
                val primary = ctx.primary()
                val powExprList = ctx.powExpr()

                Power(
                    primary = this.iVisitPrimaryExpr(primary),
                    power = powExprList.map { this.iVisitPowExpression(it) }.toTypedArray()
                )
            }

            is DegradedPowContext -> {
                this.iVisitPrimaryExpr(ctx.primary())
            }

            else -> throw IllegalArgumentException("Unsupported context: ${ctx::class.java.canonicalName}")
        }
    }

    private fun iVisitPrimaryExpr(ctx: PrimaryContext): PrimaryExpr {
        return when (ctx) {
            is NumberPrimaryContext -> {
                val numberText = ctx.NUMBER().text
                if (numberText == "INF") {
                    NumberPrimary(0, true)
                } else {
                    NumberPrimary(ctx.NUMBER().text.toDouble(), false)
                }
            }

            is FuncPrimaryContext -> {
                FunctionCallPrimary(functionName = IdentifierLiteral(ctx.IDENTIFIER().text), parameters = ctx.expr().map { this.iVisitExpr(it) }.toTypedArray())
            }

            is VarPrimaryContext -> {
                VariablePrimary(IdentifierLiteral(ctx.IDENTIFIER().text))
            }

            is ParensPrimaryContext -> {
                ParensPrimary(this.iVisitExpr(ctx.expr()))
            }

            else -> throw IllegalArgumentException("Unsupported context: ${ctx::class.java.canonicalName}")
        }
    }

    override fun visitIntegralExpression(ctx: IntegralExpressionContext): Expression {
        return when(val realCtx = ctx.integral()) {
            is DefiniteIntergalContext -> {
                val parsedExprList = realCtx.expr().map { this.iVisitExpr(it) }
                DefiniteIntegral(lower = parsedExprList[0], upper = parsedExprList[1], body = parsedExprList[2])
            }

            is IndefiniteInteralContext -> {
                IndefiniteIntegral(body = this.iVisitExpr(realCtx.expr()))
            }

            else -> throw IllegalArgumentException("Unsupported context: ${ctx::class.java.canonicalName}")
        }
    }

    override fun visitDerivativeExpression(ctx: DerivativeExpressionContext): Expression {
        return when(val realCtx = ctx.derivative()) {
            is NormalDerivativeContext -> {
                NormalDerivative(variable = IdentifierLiteral(realCtx.IDENTIFIER().text), expr = this.iVisitExpr(realCtx.expr()))
            }

            is HigherDerivativeContext -> {
                val parsedExprList = realCtx.expr().map { this.iVisitExpr(it) }
                HigherDerivative(variable = IdentifierLiteral(realCtx.IDENTIFIER().text), expr = parsedExprList[2], n = parsedExprList[0])
            }

            else -> throw IllegalArgumentException("Unsupported context: ${ctx::class.java.canonicalName}")
        }
    }
}