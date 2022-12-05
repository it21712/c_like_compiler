/**
 *  This code is part of the lab exercises for the Compilers course
 *  at Harokopio University of Athens, Dept. of Informatics and Telematics.
 */

import static java.lang.System.out;
import java_cup.runtime.Symbol;

%%

%class Lexer
%unicode
%public
%final
%integer
%line
%column
%cup

%eofval{
    return createSymbol(sym.EOF);
%eofval}

%{
    private StringBuffer sb = new StringBuffer();

    private Symbol createSymbol(int type) {
        return new Symbol(type, yyline+1, yycolumn+1);
    }

    private Symbol createSymbol(int type, Object value) {
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f] 
Comment        = "/*" [^*] ~"*/" | "/*" "*"+ "/"


Identifier     = [:jletter:] [:jletterdigit:]*
IntegerLiteral = 0 | [1-9][0-9]*


Exponent       = [eE][\+\-]?[0-9]+
Float1         = [0-9]+ \.[0-9]+{Exponent}?
Float2         = \.[0-9]+{Exponent}?
Float3         = [0-9]+\.{Exponent}?
Float4         = [0-9]+ {Exponent}

EscapeChars    = [\\n\\t\\0]
Character      = [a-zA-Z0-9]

FloatLiteral   = {Float1} | {Float2} | {Float3} | {Float4}

BooleanLiteral = (true|false)

%state STRING
%state CHAR

%%

<YYINITIAL> {
    /* reserved keywords */
        /*"print"                        { return createSymbol(sym.PRINT); }*/
        "bool"                         { return createSymbol(sym.BOOL); }
        "float"                        { return createSymbol(sym.FLOAT); }
        "int"                          { return createSymbol(sym.INT); }
        "char"                         { return createSymbol(sym.CHAR); }
        "string"                       { return createSymbol(sym.STRINGVAR); }
        "while"                        { return createSymbol(sym.WHILE); }
        "if"                           { return createSymbol(sym.IF); }
        "else"                         { return createSymbol(sym.ELSE); }
        "void"                         { return createSymbol(sym.VOID); }
        "return"                       { return createSymbol(sym.RETURN); }
        "break"                        { return createSymbol(sym.BREAK); }
        "continue"                     { return createSymbol(sym.CONTINTUE); }
        "struct"                       { return createSymbol(sym.STRUCT); }

        {BooleanLiteral}               {return createSymbol(sym.BOOLEAN_LITERAL, Boolean.valueOf(yytext())); }

        /* identifiers */
        {Identifier}                   { return createSymbol(sym.IDENTIFIER, yytext()); }

        /* literals */
        {IntegerLiteral}               { return createSymbol(sym.INTEGER_LITERAL, Integer.valueOf(yytext())); }
        {FloatLiteral}                 { return createSymbol(sym.FLOAT_LITERAL, Float.valueOf(yytext())); }


        \"                             { sb.setLength(0); yybegin(STRING); }
        \'                             { sb.setLength(0); yybegin(CHAR);}


        /* operators */
        "="                            { return createSymbol(sym.EQ); }
        "+"                            { return createSymbol(sym.PLUS); }
        "-"                            { return createSymbol(sym.MINUS); }
        "*"                            { return createSymbol(sym.TIMES); }
        "/"                            { return createSymbol(sym.DIVISION); }
        "%"                            { return createSymbol(sym.MODULO); }
        "=="                           { return createSymbol(sym.EQEQ); }
        "&&"                           { return createSymbol(sym.AND); }
        "||"                           { return createSymbol(sym.OR); }
        "!"                            { return createSymbol(sym.NOT); }
        "."                            { return createSymbol(sym.DOT); }
        ">"                            { return createSymbol(sym.GT); }
        "<"                            { return createSymbol(sym.LT); }
        "! ="                          { return createSymbol(sym.NEQ); }
        "!="                           { return createSymbol(sym.NEQ); }
        "<="                           { return createSymbol(sym.LE); }
        ">="                           { return createSymbol(sym.GE); }


        /* seperators */
        "{"                            { return createSymbol(sym.LCURLY); }
        "}"                            { return createSymbol(sym.RCURLY); }
        "["                            { return createSymbol(sym.LBRACKET); }
        "]"                            { return createSymbol(sym.RBRACKET); }
        "("                            { return createSymbol(sym.LPAREN); }
        ")"                            { return createSymbol(sym.RPAREN); }
        ";"                            { return createSymbol(sym.SEMICOLON); }
        ","                            { return createSymbol(sym.COMMA); }


        /* comments */
        {Comment}                      { /* ignore */ }

        /* whitespace */
        {WhiteSpace}                   { /* ignore */ }
    }

    <STRING> {
        \"                             { yybegin(YYINITIAL);
                                         return createSymbol(sym.STRING_LITERAL, sb.toString());
                                       }

        [^\n\r\"\\]+                   { sb.append(yytext()); }
        \\t                            { sb.append('\t'); }
        \\n                            { sb.append('\n'); }
        \\r                            { sb.append('\r'); }
        \\\"                           { sb.append('\"'); }
        \\                             { sb.append('\\'); }
    }

    <CHAR>{

       /* allowed escape chars */
       \\t\'                             { sb.append("\t"); yybegin(YYINITIAL); return createSymbol(sym.CHAR_LITERAL, sb.toString()); }
       \\n\'                             { sb.append("\n"); yybegin(YYINITIAL); return createSymbol(sym.CHAR_LITERAL, sb.toString()); }
       \\0\'                             { sb.append("\0"); yybegin(YYINITIAL); return createSymbol(sym.CHAR_LITERAL, sb.toString()); }


       /* single character */
       {Character}\'                     { sb.append(yytext()); yybegin(YYINITIAL); sb.deleteCharAt(sb.length()-1); return createSymbol(sym.CHAR_LITERAL, sb.toString()); }

       /* invalid expressions*/
       [^]                               {throw new RuntimeException((yyline+1) + ":" + (yycolumn+1) + ": multi-character character constants are not allowed");}

    }

/* error fallback */
[^]                                { throw new RuntimeException((yyline+1) + ":" + (yycolumn+1) + ": illegal character <"+ yytext()+">"); }
