package ast;

public class ExprDotIdentifierExpression extends Expression {

    private Expression expression;
    private String identifier;

    public ExprDotIdentifierExpression(Expression expression, String identifier) {
        this.expression = expression;
        this.identifier = identifier;
    }


    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
