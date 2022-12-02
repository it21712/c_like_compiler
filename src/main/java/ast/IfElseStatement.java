package ast;

import javax.annotation.Nullable;

public class IfElseStatement extends Statement {

    private Expression expression;
    private Statement ifStatement;
    private Statement elseStatement;

    public IfElseStatement(Expression expression, Statement ifStatement, Statement elseStatement) {
        this.expression = expression;
        this.ifStatement = ifStatement;
        this.elseStatement = elseStatement;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Statement getIfStatement() {
        return ifStatement;
    }

    public void setIfStatement(Statement ifStatement) {
        this.ifStatement = ifStatement;
    }

    public Statement getElseStatement() {
        return elseStatement;
    }

    public void setElseStatement(Statement elseStatement) {
        this.elseStatement = elseStatement;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }

    public boolean bothReturn(){
        return ifStatement.returnsValue() && elseStatement.returnsValue();
    }
}
