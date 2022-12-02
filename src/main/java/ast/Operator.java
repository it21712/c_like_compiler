/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package ast;

public enum Operator {

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVISION("/"),
    MODULO("%"),
    EQUAL("=="),
    NEQUAL("!="),
    NOT("!"),
    AND("&&"),
    OR("||"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    DOT("."),
    ;

    private String type;

    Operator(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

    public boolean isUnary() {
        return this.equals(Operator.MINUS) || this.equals(Operator.NOT);
    }

    public boolean isRelational() {
        return this.equals(Operator.EQUAL) || this.equals(Operator.NEQUAL)
                || this.equals(Operator.GT) || this.equals(Operator.GE)
                || this.equals(Operator.LT) || this.equals(Operator.LE);
    }

    public boolean isLogical(){
        return this.equals(Operator.EQUAL) || this.equals(Operator.NEQUAL)
                || this.equals(Operator.AND) || this.equals(Operator.OR)
                || this.equals(NOT); //TODO ADD NOT
    }

    public boolean isMathematical(){
        return this.equals(Operator.PLUS) || this.equals(Operator.MINUS)
                || this.equals(Operator.MULTIPLY) || this.equals(Operator.DIVISION)
                || this.equals(Operator.MODULO);
    }

}
