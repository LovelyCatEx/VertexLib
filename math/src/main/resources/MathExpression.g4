grammar MathExpression;

prog: expr EOF ;

expr
    : addExpr     # AddExpression
    | integral    # IntegralExpression
    | derivative  # DerivativeExpression
    ;

addExpr
    : mulExpr ( ('+' | '-') mulExpr )*
    ;

mulExpr
    : unaryExpr ( ('*' | '/') unaryExpr )*
    ;

unaryExpr
    : ('+' | '-') unaryExpr     # UnaryExpression
    | powExpr                   # DegradedUnary
    ;

powExpr
    : primary                   # DegradedPow
    | primary ( '^' powExpr )*  # PowExpression
    ;

primary
    : NUMBER                       # NumberPrimary
    | IDENTIFIER '(' expr (',' expr)* ')'      # FuncPrimary
    | IDENTIFIER                   # VarPrimary
    | '(' expr ')'                 # ParensPrimary
    ;


integral
    : 'int' lower=expr '^' upper=expr '{' body=expr '}' # DefiniteIntergal
    | 'int' '{' body=expr '}'                           # IndefiniteInteral
    ;

derivative
    : 'd' '/' 'd' IDENTIFIER expr                       # NormalDerivative
    | 'd' '^' expr '/' 'd' '^' expr IDENTIFIER expr     # HigherDerivative
    ;

NUMBER : [0-9]+ ('.' [0-9]+)? ([eE][+-]?[0-9]+)? | INFINITE;
INFINITE: 'INF';
IDENTIFIER : [a-zA-Z_] [a-zA-Z_0-9]* {
   if (getText().equals("INF")) {
       setType(INFINITE);
   }
};
WS : [ \t\r\n]+ -> skip ;