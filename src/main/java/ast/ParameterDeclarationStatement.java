package ast;

import  org.objectweb.asm.Type;

public class ParameterDeclarationStatement extends Statement{


    private Type type;
    private String structIdentifier = "";
    private String identifier;
    private boolean isArray = false;


    /** variable parameter constructor **/
    public ParameterDeclarationStatement(Type type, String identifier){
        this.type = type;
        this.identifier = identifier;
    }

    /** variable array parameter constructor **/
    public ParameterDeclarationStatement(Type type, String identifier, boolean isArray){
        this.type = type;
        this.identifier = identifier;
        this.isArray = isArray;
    }

    /** struct parameter constructor **/
    public ParameterDeclarationStatement(Type type, String structIdentifier, String identifier){
        this.type = type;
        this.structIdentifier = structIdentifier;
        this.identifier = identifier;
    }

    /** struct array parameter constructor **/
    public ParameterDeclarationStatement(Type type, String structIdentifier, String identifier, boolean isArray){
        this.type = type;
        this.structIdentifier = structIdentifier;
        this.identifier = identifier;
        this.isArray = isArray;
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

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
