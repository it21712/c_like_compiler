package ast;

public class ExpressionDotArrayElementExpression extends Expression {

    private Expression expression;
    private IdentifierBracketExpression identifierBracketExpression;

    public ExpressionDotArrayElementExpression(Expression expression, IdentifierBracketExpression identifierBracketExpression) {
        this.expression = expression;
        this.identifierBracketExpression = identifierBracketExpression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public IdentifierBracketExpression getIdentifierBracketExpression() {
        return identifierBracketExpression;
    }

    public void setIdentifierBracketExpression(IdentifierBracketExpression identifierBracketExpression) {
        this.identifierBracketExpression = identifierBracketExpression;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
