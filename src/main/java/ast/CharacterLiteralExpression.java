package ast;

public class CharacterLiteralExpression extends Expression {

    private Character literal;

    public CharacterLiteralExpression(char literal) {
        this.literal = literal;
    }

    public Character getLiteral() {
        return literal;
    }

    public void setLiteral(char literal) {
        this.literal = literal;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
