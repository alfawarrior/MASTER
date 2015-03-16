grammar Expression;

// Parser rules:

expression :
        '(' expression ')'                                      # Bracketed
    |   '{' expression (',' expression)* '}'                    # Array
    |   expression '[' expression ']'                           # ArraySubscript
    |   IDENT '(' expression (',' expression)* ')'              # Function
    |   op=(EXP|LOG|SQRT|SUM|THETA|ABS) '(' expression ')'      # UnaryOp
    |   '-' expression                                          # Negation
    |   expression '!'                                          # Factorial
    |<assoc=right> expression '^' expression                    # Exponentiation
    |   expression op=('*'|'/'|'%') expression                  # MulDiv
    |   expression op=('+'|'-') expression                      # AddSub
    |   expression op=('=='|'!='|'<'|'>'|'<='|'>=') expression  # Equality
    |   expression op=('&&'|'||') expression                    # BooleanOp
    |<assoc=right>   expression '?' expression ':' expression   # IfThenElse
    |   IDENT                                                   # Variable
    |   val=(NNFLOAT | NNINT)                                   # Number
    ;


// Lexer rules:

ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
MOD : '%' ;
POW : '^' ;

EXP : 'exp' ;
LOG : 'log' ;
SQRT : 'sqrt' ;
SUM : 'sum' ;
THETA : 'theta' ;
ABS : 'abs' ;

AND : '&&' ;
OR : '||' ;

EQ: '==';
GT: '>';
LT: '<';
GE: '>=';
LE: '<=';
NE: '!=';

NNINT : '0' | NZD D* ;
NNFLOAT : NNINT ('.' D*) ([eE] '-'? D+)? ;
fragment D : [0-9] ;
fragment NZD : [1-9] ;

IDENT : [a-zA-Z_][a-zA-Z_0-9]* ;

COMMENT_SINGLELINE: '//' .*? '\n' -> skip ;
COMMENT_MULTILINE: '/*' .*? '*/' -> skip ;

WHITESPACE : [ \t\r\n]+ -> skip ;
