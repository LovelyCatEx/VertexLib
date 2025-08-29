package com.lovelycatv.vertex.math.expr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MathExpressionParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, NUMBER=13, INFINITE=14, IDENTIFIER=15, WS=16;
	public static final int
		RULE_prog = 0, RULE_expr = 1, RULE_addExpr = 2, RULE_mulExpr = 3, RULE_unaryExpr = 4, 
		RULE_powExpr = 5, RULE_primary = 6, RULE_integral = 7, RULE_derivative = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "expr", "addExpr", "mulExpr", "unaryExpr", "powExpr", "primary", 
			"integral", "derivative"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'+'", "'-'", "'*'", "'/'", "'^'", "'('", "','", "')'", "'int'", 
			"'{'", "'}'", "'d'", null, "'INF'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "NUMBER", "INFINITE", "IDENTIFIER", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "MathExpression.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MathExpressionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode EOF() { return getToken(MathExpressionParser.EOF, 0); }
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			expr();
			setState(19);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DerivativeExpressionContext extends ExprContext {
		public DerivativeContext derivative() {
			return getRuleContext(DerivativeContext.class,0);
		}
		public DerivativeExpressionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterDerivativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitDerivativeExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitDerivativeExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AddExpressionContext extends ExprContext {
		public AddExprContext addExpr() {
			return getRuleContext(AddExprContext.class,0);
		}
		public AddExpressionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterAddExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitAddExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitAddExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntegralExpressionContext extends ExprContext {
		public IntegralContext integral() {
			return getRuleContext(IntegralContext.class,0);
		}
		public IntegralExpressionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterIntegralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitIntegralExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitIntegralExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expr);
		try {
			setState(24);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__5:
			case NUMBER:
			case IDENTIFIER:
				_localctx = new AddExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(21);
				addExpr();
				}
				break;
			case T__8:
				_localctx = new IntegralExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(22);
				integral();
				}
				break;
			case T__11:
				_localctx = new DerivativeExpressionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(23);
				derivative();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AddExprContext extends ParserRuleContext {
		public List<MulExprContext> mulExpr() {
			return getRuleContexts(MulExprContext.class);
		}
		public MulExprContext mulExpr(int i) {
			return getRuleContext(MulExprContext.class,i);
		}
		public AddExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_addExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterAddExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitAddExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitAddExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AddExprContext addExpr() throws RecognitionException {
		AddExprContext _localctx = new AddExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_addExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			mulExpr();
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0 || _la==T__1) {
				{
				{
				setState(27);
				_la = _input.LA(1);
				if ( !(_la==T__0 || _la==T__1) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(28);
				mulExpr();
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MulExprContext extends ParserRuleContext {
		public List<UnaryExprContext> unaryExpr() {
			return getRuleContexts(UnaryExprContext.class);
		}
		public UnaryExprContext unaryExpr(int i) {
			return getRuleContext(UnaryExprContext.class,i);
		}
		public MulExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mulExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterMulExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitMulExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitMulExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MulExprContext mulExpr() throws RecognitionException {
		MulExprContext _localctx = new MulExprContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_mulExpr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			unaryExpr();
			setState(39);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(35);
					_la = _input.LA(1);
					if ( !(_la==T__2 || _la==T__3) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(36);
					unaryExpr();
					}
					} 
				}
				setState(41);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryExprContext extends ParserRuleContext {
		public UnaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpr; }
	 
		public UnaryExprContext() { }
		public void copyFrom(UnaryExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class UnaryExpressionContext extends UnaryExprContext {
		public UnaryExprContext unaryExpr() {
			return getRuleContext(UnaryExprContext.class,0);
		}
		public UnaryExpressionContext(UnaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterUnaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitUnaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitUnaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DegradedUnaryContext extends UnaryExprContext {
		public PowExprContext powExpr() {
			return getRuleContext(PowExprContext.class,0);
		}
		public DegradedUnaryContext(UnaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterDegradedUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitDegradedUnary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitDegradedUnary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExprContext unaryExpr() throws RecognitionException {
		UnaryExprContext _localctx = new UnaryExprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_unaryExpr);
		int _la;
		try {
			setState(45);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
				_localctx = new UnaryExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(42);
				_la = _input.LA(1);
				if ( !(_la==T__0 || _la==T__1) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(43);
				unaryExpr();
				}
				break;
			case T__5:
			case NUMBER:
			case IDENTIFIER:
				_localctx = new DegradedUnaryContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(44);
				powExpr();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PowExprContext extends ParserRuleContext {
		public PowExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_powExpr; }
	 
		public PowExprContext() { }
		public void copyFrom(PowExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PowExpressionContext extends PowExprContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public List<PowExprContext> powExpr() {
			return getRuleContexts(PowExprContext.class);
		}
		public PowExprContext powExpr(int i) {
			return getRuleContext(PowExprContext.class,i);
		}
		public PowExpressionContext(PowExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterPowExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitPowExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitPowExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DegradedPowContext extends PowExprContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public DegradedPowContext(PowExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterDegradedPow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitDegradedPow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitDegradedPow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowExprContext powExpr() throws RecognitionException {
		PowExprContext _localctx = new PowExprContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_powExpr);
		try {
			int _alt;
			setState(56);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				_localctx = new DegradedPowContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(47);
				primary();
				}
				break;
			case 2:
				_localctx = new PowExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(48);
				primary();
				setState(53);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(49);
						match(T__4);
						setState(50);
						powExpr();
						}
						} 
					}
					setState(55);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimaryContext extends ParserRuleContext {
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
	 
		public PrimaryContext() { }
		public void copyFrom(PrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FuncPrimaryContext extends PrimaryContext {
		public TerminalNode IDENTIFIER() { return getToken(MathExpressionParser.IDENTIFIER, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public FuncPrimaryContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterFuncPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitFuncPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitFuncPrimary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberPrimaryContext extends PrimaryContext {
		public TerminalNode NUMBER() { return getToken(MathExpressionParser.NUMBER, 0); }
		public NumberPrimaryContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterNumberPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitNumberPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitNumberPrimary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarPrimaryContext extends PrimaryContext {
		public TerminalNode IDENTIFIER() { return getToken(MathExpressionParser.IDENTIFIER, 0); }
		public VarPrimaryContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterVarPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitVarPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitVarPrimary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParensPrimaryContext extends PrimaryContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParensPrimaryContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterParensPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitParensPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitParensPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_primary);
		int _la;
		try {
			setState(76);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new NumberPrimaryContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(58);
				match(NUMBER);
				}
				break;
			case 2:
				_localctx = new FuncPrimaryContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(59);
				match(IDENTIFIER);
				setState(60);
				match(T__5);
				setState(61);
				expr();
				setState(66);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__6) {
					{
					{
					setState(62);
					match(T__6);
					setState(63);
					expr();
					}
					}
					setState(68);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(69);
				match(T__7);
				}
				break;
			case 3:
				_localctx = new VarPrimaryContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(71);
				match(IDENTIFIER);
				}
				break;
			case 4:
				_localctx = new ParensPrimaryContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(72);
				match(T__5);
				setState(73);
				expr();
				setState(74);
				match(T__7);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntegralContext extends ParserRuleContext {
		public IntegralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integral; }
	 
		public IntegralContext() { }
		public void copyFrom(IntegralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IndefiniteInteralContext extends IntegralContext {
		public ExprContext body;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public IndefiniteInteralContext(IntegralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterIndefiniteInteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitIndefiniteInteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitIndefiniteInteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DefiniteIntergalContext extends IntegralContext {
		public ExprContext lower;
		public ExprContext upper;
		public ExprContext body;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public DefiniteIntergalContext(IntegralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterDefiniteIntergal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitDefiniteIntergal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitDefiniteIntergal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegralContext integral() throws RecognitionException {
		IntegralContext _localctx = new IntegralContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_integral);
		try {
			setState(91);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				_localctx = new DefiniteIntergalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(78);
				match(T__8);
				setState(79);
				((DefiniteIntergalContext)_localctx).lower = expr();
				setState(80);
				match(T__4);
				setState(81);
				((DefiniteIntergalContext)_localctx).upper = expr();
				setState(82);
				match(T__9);
				setState(83);
				((DefiniteIntergalContext)_localctx).body = expr();
				setState(84);
				match(T__10);
				}
				break;
			case 2:
				_localctx = new IndefiniteInteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(86);
				match(T__8);
				setState(87);
				match(T__9);
				setState(88);
				((IndefiniteInteralContext)_localctx).body = expr();
				setState(89);
				match(T__10);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DerivativeContext extends ParserRuleContext {
		public DerivativeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_derivative; }
	 
		public DerivativeContext() { }
		public void copyFrom(DerivativeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NormalDerivativeContext extends DerivativeContext {
		public TerminalNode IDENTIFIER() { return getToken(MathExpressionParser.IDENTIFIER, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NormalDerivativeContext(DerivativeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterNormalDerivative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitNormalDerivative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitNormalDerivative(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class HigherDerivativeContext extends DerivativeContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(MathExpressionParser.IDENTIFIER, 0); }
		public HigherDerivativeContext(DerivativeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).enterHigherDerivative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MathExpressionListener ) ((MathExpressionListener)listener).exitHigherDerivative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MathExpressionVisitor ) return ((MathExpressionVisitor<? extends T>)visitor).visitHigherDerivative(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DerivativeContext derivative() throws RecognitionException {
		DerivativeContext _localctx = new DerivativeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_derivative);
		try {
			setState(108);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new NormalDerivativeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(93);
				match(T__11);
				setState(94);
				match(T__3);
				setState(95);
				match(T__11);
				setState(96);
				match(IDENTIFIER);
				setState(97);
				expr();
				}
				break;
			case 2:
				_localctx = new HigherDerivativeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(98);
				match(T__11);
				setState(99);
				match(T__4);
				setState(100);
				expr();
				setState(101);
				match(T__3);
				setState(102);
				match(T__11);
				setState(103);
				match(T__4);
				setState(104);
				expr();
				setState(105);
				match(IDENTIFIER);
				setState(106);
				expr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\22q\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2"+
		"\3\3\3\3\3\3\5\3\33\n\3\3\4\3\4\3\4\7\4 \n\4\f\4\16\4#\13\4\3\5\3\5\3"+
		"\5\7\5(\n\5\f\5\16\5+\13\5\3\6\3\6\3\6\5\6\60\n\6\3\7\3\7\3\7\3\7\7\7"+
		"\66\n\7\f\7\16\79\13\7\5\7;\n\7\3\b\3\b\3\b\3\b\3\b\3\b\7\bC\n\b\f\b\16"+
		"\bF\13\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bO\n\b\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t^\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\no\n\n\3\n\2\2\13\2\4\6\b\n\f\16\20\22"+
		"\2\4\3\2\3\4\3\2\5\6\2t\2\24\3\2\2\2\4\32\3\2\2\2\6\34\3\2\2\2\b$\3\2"+
		"\2\2\n/\3\2\2\2\f:\3\2\2\2\16N\3\2\2\2\20]\3\2\2\2\22n\3\2\2\2\24\25\5"+
		"\4\3\2\25\26\7\2\2\3\26\3\3\2\2\2\27\33\5\6\4\2\30\33\5\20\t\2\31\33\5"+
		"\22\n\2\32\27\3\2\2\2\32\30\3\2\2\2\32\31\3\2\2\2\33\5\3\2\2\2\34!\5\b"+
		"\5\2\35\36\t\2\2\2\36 \5\b\5\2\37\35\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\""+
		"\3\2\2\2\"\7\3\2\2\2#!\3\2\2\2$)\5\n\6\2%&\t\3\2\2&(\5\n\6\2\'%\3\2\2"+
		"\2(+\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*\t\3\2\2\2+)\3\2\2\2,-\t\2\2\2-\60\5"+
		"\n\6\2.\60\5\f\7\2/,\3\2\2\2/.\3\2\2\2\60\13\3\2\2\2\61;\5\16\b\2\62\67"+
		"\5\16\b\2\63\64\7\7\2\2\64\66\5\f\7\2\65\63\3\2\2\2\669\3\2\2\2\67\65"+
		"\3\2\2\2\678\3\2\2\28;\3\2\2\29\67\3\2\2\2:\61\3\2\2\2:\62\3\2\2\2;\r"+
		"\3\2\2\2<O\7\17\2\2=>\7\21\2\2>?\7\b\2\2?D\5\4\3\2@A\7\t\2\2AC\5\4\3\2"+
		"B@\3\2\2\2CF\3\2\2\2DB\3\2\2\2DE\3\2\2\2EG\3\2\2\2FD\3\2\2\2GH\7\n\2\2"+
		"HO\3\2\2\2IO\7\21\2\2JK\7\b\2\2KL\5\4\3\2LM\7\n\2\2MO\3\2\2\2N<\3\2\2"+
		"\2N=\3\2\2\2NI\3\2\2\2NJ\3\2\2\2O\17\3\2\2\2PQ\7\13\2\2QR\5\4\3\2RS\7"+
		"\7\2\2ST\5\4\3\2TU\7\f\2\2UV\5\4\3\2VW\7\r\2\2W^\3\2\2\2XY\7\13\2\2YZ"+
		"\7\f\2\2Z[\5\4\3\2[\\\7\r\2\2\\^\3\2\2\2]P\3\2\2\2]X\3\2\2\2^\21\3\2\2"+
		"\2_`\7\16\2\2`a\7\6\2\2ab\7\16\2\2bc\7\21\2\2co\5\4\3\2de\7\16\2\2ef\7"+
		"\7\2\2fg\5\4\3\2gh\7\6\2\2hi\7\16\2\2ij\7\7\2\2jk\5\4\3\2kl\7\21\2\2l"+
		"m\5\4\3\2mo\3\2\2\2n_\3\2\2\2nd\3\2\2\2o\23\3\2\2\2\f\32!)/\67:DN]n";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}