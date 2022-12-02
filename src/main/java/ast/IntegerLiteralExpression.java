/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package ast;

public class IntegerLiteralExpression extends Expression {

    private java.lang.Integer literal;

    public IntegerLiteralExpression(java.lang.Integer literal) {
        this.literal = literal;
    }

    public java.lang.Integer getLiteral() {
        return literal;
    }

    public void setLiteral(java.lang.Integer literal) {
        this.literal = literal;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }

}
