package ast;

import java.util.ArrayList;
import java.util.List;
import  org.objectweb.asm.Type;

public class FunctionDefinitionStatement extends Statement {

    private Type type;
    private String structIdentifier = "";
    private String identifier;
    private List<ParameterDeclarationStatement> parameters = new ArrayList<>();
    //private CompoundStatement statementList;
    private List<Statement> statements = new ArrayList<>();

    /**simple function constructor**/
    /*public FunctionDefinitionStatement(Type type, String identifier, List<ParameterDeclarationStatement> parameters, CompoundStatement statementList){
        this.type = type;
        this.identifier = identifier;
        this.parameters = parameters;
        this.statementList = statementList;
    }*/
    public FunctionDefinitionStatement(Type type, String identifier, List<ParameterDeclarationStatement> parameters, List<Statement> statements){
        this.type = type;
        this.identifier = identifier;
        this.parameters = parameters;
        this.statements = statements;
    }
    /**struct function constructor**/
    /*public FunctionDefinitionStatement(Type type, String structIdentifier, String identifier, List<ParameterDeclarationStatement> parameters, CompoundStatement statementList){
        this.type = type;
        this.structIdentifier = structIdentifier;
        this.identifier = identifier;
        this.parameters = parameters;
        this.statementList = statementList;
    }*/
    public FunctionDefinitionStatement(Type type, String structIdentifier, String identifier, List<ParameterDeclarationStatement> parameters, List<Statement> statements){
        this.type = type;
        this.structIdentifier = structIdentifier;
        this.identifier = identifier;
        this.parameters = parameters;
        this.statements = statements;
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

    public List<ParameterDeclarationStatement> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDeclarationStatement> parameters) {
        this.parameters = parameters;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    /*public CompoundStatement getStatementList() {
        return statementList;
    }

    public void setStatementList(CompoundStatement statementList) {
        this.statementList = statementList;
    }*/

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
