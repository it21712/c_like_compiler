package ast;

import java.util.ArrayList;
import java.util.List;

public class IdentifierParenthesisExpression extends Expression {

    private String identifier;
    private List<Expression> expressions = new ArrayList<>();

    public IdentifierParenthesisExpression(String identifier){
        this.identifier = identifier;
    }

    public IdentifierParenthesisExpression(String identifier, List<Expression> expressions) {
        this.identifier = identifier;
        this.expressions = expressions;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
