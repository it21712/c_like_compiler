/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package ast;

public class AssignmentStatement extends Statement {

    private Expression expression0;
    private Expression expression;

    public AssignmentStatement(Expression expression0, Expression expression) {
        this.expression0 = expression0;
        this.expression = expression;
    }

    public Expression getExpression0() {
        return expression0;
    }

    public void setExpression0(Expression expression0) {
        this.expression0 = expression0;
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
