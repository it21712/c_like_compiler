package ast;

import org.objectweb.asm.Type;

public class VariableArrayDefinition extends VariableDefinitionStatement {

    private Type type;
    private String identifier;
    private Integer literal;

    public VariableArrayDefinition(Type type, String identifier, Integer literal) {
        this.type = type;
        this.identifier = identifier;
        this.literal = literal;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getLiteral() {
        return literal;
    }

    public void setLiteral(Integer literal) {
        this.literal = literal;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }

    @Override
    public String getId(){
        return getIdentifier();
    }
}
