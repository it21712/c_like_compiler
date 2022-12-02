package ast;


import ast.ASTVisitor;
import ast.ASTVisitorException;
import ast.Statement;
import org.objectweb.asm.Type;

import java.util.List;

public class StructDefinitionStatement extends Statement {

    private Type type;
    private String identifier;
    private List<VariableDefinitionStatement> variableDefinitions;

    public StructDefinitionStatement(Type type, String identifier, List<VariableDefinitionStatement> variableDefinitions) {
        this.type = type;
        this.identifier = identifier;
        this.variableDefinitions = variableDefinitions;
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

    public List<VariableDefinitionStatement> getVariableDefinitions() {
        return variableDefinitions;
    }

    public void setVariableDefinitions(List<VariableDefinitionStatement> variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
