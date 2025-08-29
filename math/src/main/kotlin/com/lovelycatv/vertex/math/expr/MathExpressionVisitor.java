package com.lovelycatv.vertex.math.expr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MathExpressionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MathExpressionVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MathExpressionParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(MathExpressionParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpression(MathExpressionParser.AddExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntegralExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegralExpression(MathExpressionParser.IntegralExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DerivativeExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDerivativeExpression(MathExpressionParser.DerivativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MathExpressionParser#addExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(MathExpressionParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MathExpressionParser#mulExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExpr(MathExpressionParser.MulExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryExpression}
	 * labeled alternative in {@link MathExpressionParser#unaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpression(MathExpressionParser.UnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DegradedUnary}
	 * labeled alternative in {@link MathExpressionParser#unaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDegradedUnary(MathExpressionParser.DegradedUnaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DegradedPow}
	 * labeled alternative in {@link MathExpressionParser#powExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDegradedPow(MathExpressionParser.DegradedPowContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PowExpression}
	 * labeled alternative in {@link MathExpressionParser#powExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowExpression(MathExpressionParser.PowExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberPrimary(MathExpressionParser.NumberPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FuncPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncPrimary(MathExpressionParser.FuncPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarPrimary(MathExpressionParser.VarPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParensPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParensPrimary(MathExpressionParser.ParensPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DefiniteIntergal}
	 * labeled alternative in {@link MathExpressionParser#integral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefiniteIntergal(MathExpressionParser.DefiniteIntergalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IndefiniteInteral}
	 * labeled alternative in {@link MathExpressionParser#integral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndefiniteInteral(MathExpressionParser.IndefiniteInteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NormalDerivative}
	 * labeled alternative in {@link MathExpressionParser#derivative}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormalDerivative(MathExpressionParser.NormalDerivativeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code HigherDerivative}
	 * labeled alternative in {@link MathExpressionParser#derivative}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHigherDerivative(MathExpressionParser.HigherDerivativeContext ctx);
}