package ast;

public class ExpressionSemicolonStatement extends Statement{


    private Expression expression;

    public ExpressionSemicolonStatement(Expression e) {
        this.expression = e;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
