package ast;

import org.objectweb.asm.Type;

public class StructDeclarationStatement extends VariableDefinitionStatement {

    private Type type;
    private String structIdentifier;
    private String identifier;

    public StructDeclarationStatement(Type type, String structIdentifier, String identifier) {
        this.type = type;
        this.structIdentifier = structIdentifier;
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getStructIdentifier() {
        return structIdentifier;
    }

    public void setStructIdentifier(String structIdentifier) {
        this.structIdentifier = structIdentifier;
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

    @Override
    public String getId(){
        return getIdentifier();
    }
}
