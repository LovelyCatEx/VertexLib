package com.lovelycatv.vertex.math.expr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MathExpressionParser}.
 */
public interface MathExpressionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MathExpressionParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(MathExpressionParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link MathExpressionParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(MathExpressionParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddExpression(MathExpressionParser.AddExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddExpression(MathExpressionParser.AddExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IntegralExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIntegralExpression(MathExpressionParser.IntegralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IntegralExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIntegralExpression(MathExpressionParser.IntegralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DerivativeExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDerivativeExpression(MathExpressionParser.DerivativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DerivativeExpression}
	 * labeled alternative in {@link MathExpressionParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDerivativeExpression(MathExpressionParser.DerivativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MathExpressionParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void enterAddExpr(MathExpressionParser.AddExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MathExpressionParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void exitAddExpr(MathExpressionParser.AddExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MathExpressionParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void enterMulExpr(MathExpressionParser.MulExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MathExpressionParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void exitMulExpr(MathExpressionParser.MulExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryExpression}
	 * labeled alternative in {@link MathExpressionParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(MathExpressionParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryExpression}
	 * labeled alternative in {@link MathExpressionParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(MathExpressionParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DegradedUnary}
	 * labeled alternative in {@link MathExpressionParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterDegradedUnary(MathExpressionParser.DegradedUnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DegradedUnary}
	 * labeled alternative in {@link MathExpressionParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitDegradedUnary(MathExpressionParser.DegradedUnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DegradedPow}
	 * labeled alternative in {@link MathExpressionParser#powExpr}.
	 * @param ctx the parse tree
	 */
	void enterDegradedPow(MathExpressionParser.DegradedPowContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DegradedPow}
	 * labeled alternative in {@link MathExpressionParser#powExpr}.
	 * @param ctx the parse tree
	 */
	void exitDegradedPow(MathExpressionParser.DegradedPowContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PowExpression}
	 * labeled alternative in {@link MathExpressionParser#powExpr}.
	 * @param ctx the parse tree
	 */
	void enterPowExpression(MathExpressionParser.PowExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PowExpression}
	 * labeled alternative in {@link MathExpressionParser#powExpr}.
	 * @param ctx the parse tree
	 */
	void exitPowExpression(MathExpressionParser.PowExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterNumberPrimary(MathExpressionParser.NumberPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitNumberPrimary(MathExpressionParser.NumberPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FuncPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterFuncPrimary(MathExpressionParser.FuncPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FuncPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitFuncPrimary(MathExpressionParser.FuncPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VarPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterVarPrimary(MathExpressionParser.VarPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VarPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitVarPrimary(MathExpressionParser.VarPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParensPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterParensPrimary(MathExpressionParser.ParensPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParensPrimary}
	 * labeled alternative in {@link MathExpressionParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitParensPrimary(MathExpressionParser.ParensPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DefiniteIntergal}
	 * labeled alternative in {@link MathExpressionParser#integral}.
	 * @param ctx the parse tree
	 */
	void enterDefiniteIntergal(MathExpressionParser.DefiniteIntergalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DefiniteIntergal}
	 * labeled alternative in {@link MathExpressionParser#integral}.
	 * @param ctx the parse tree
	 */
	void exitDefiniteIntergal(MathExpressionParser.DefiniteIntergalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IndefiniteInteral}
	 * labeled alternative in {@link MathExpressionParser#integral}.
	 * @param ctx the parse tree
	 */
	void enterIndefiniteInteral(MathExpressionParser.IndefiniteInteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IndefiniteInteral}
	 * labeled alternative in {@link MathExpressionParser#integral}.
	 * @param ctx the parse tree
	 */
	void exitIndefiniteInteral(MathExpressionParser.IndefiniteInteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NormalDerivative}
	 * labeled alternative in {@link MathExpressionParser#derivative}.
	 * @param ctx the parse tree
	 */
	void enterNormalDerivative(MathExpressionParser.NormalDerivativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NormalDerivative}
	 * labeled alternative in {@link MathExpressionParser#derivative}.
	 * @param ctx the parse tree
	 */
	void exitNormalDerivative(MathExpressionParser.NormalDerivativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code HigherDerivative}
	 * labeled alternative in {@link MathExpressionParser#derivative}.
	 * @param ctx the parse tree
	 */
	void enterHigherDerivative(MathExpressionParser.HigherDerivativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code HigherDerivative}
	 * labeled alternative in {@link MathExpressionParser#derivative}.
	 * @param ctx the parse tree
	 */
	void exitHigherDerivative(MathExpressionParser.HigherDerivativeContext ctx);
}