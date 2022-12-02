package ast;

import org.objectweb.asm.Type;

public class VariableDefinition extends VariableDefinitionStatement{


    private Type type;
    //private TypeSpecifier type;
    private String identifier;


    public VariableDefinition(Type type, String identifier) {
        this.type = type;
        this.identifier = identifier;
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

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }

    @Override
    public String getId(){
        return getIdentifier();
    }
}
