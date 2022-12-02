package ast;

public class BooleanLiteralExpression extends Expression {

    private boolean literal;

    public BooleanLiteralExpression(boolean literal) {
        this.literal = literal;
    }

    public boolean getLiteral() {
        return literal;
    }

    public void setLiteral(boolean literal) {
        this.literal = literal;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
