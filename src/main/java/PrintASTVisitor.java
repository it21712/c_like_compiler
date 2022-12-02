/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
import ast.*;

import ast.IntegerLiteralExpression;
import org.apache.commons.lang3.StringEscapeUtils;

public class PrintASTVisitor implements ASTVisitor {

    @Override
    public void visit(CompUnit node) throws ASTVisitorException {
        for (Statement s : node.getStatements()) {
            s.accept(this);
        }
    }

    @Override
    public void visit(RootUnit node) throws ASTVisitorException {
        for (CompUnit c : node.getCompUnits()) {
            c.accept(this);
        }
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {
        node.getExpression0().accept(this);
        System.out.print(" = ");
        node.getExpression().accept(this);
        System.out.println(";");
    }

    @Override
    public void visit(PrintStatement node) throws ASTVisitorException {
        System.out.print("print(");
        node.getExpression().accept(this);
        System.out.println(");");
    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        System.out.print(" ");
        System.out.print(node.getOperator());
        System.out.print(" ");
        node.getExpression2().accept(this);
    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        System.out.print(node.getOperator());
        System.out.print(" ");
        node.getExpression().accept(this);
    }

    @Override
    public void visit(IdentifierExpression node) throws ASTVisitorException {
        System.out.print(node.getIdentifier());
    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        System.out.print(node.getLiteral());
    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        System.out.print(node.getLiteral());
    }
    
    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        System.out.print("\"");
        System.out.print(StringEscapeUtils.escapeJava(node.getLiteral()));
        System.out.print("\"");
    }

    @Override
    public void visit(CharLiteralExpression node) throws ASTVisitorException {
        System.out.print(node.getLiteral());
    }

    @Override
    public void visit(BooleanLiteralExpression node) throws ASTVisitorException {
        System.out.print(node.getLiteral());
    }

    @Override
    public void visit(CharacterLiteralExpression node) throws ASTVisitorException {
        System.out.print(node.getLiteral());
    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        System.out.print("( ");
        node.getExpression().accept(this);
        System.out.print(" )");
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        System.out.print("while(");
        node.getExpression().accept(this);
        System.out.print(")");
        node.getStatement().accept(this);
    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        System.out.print("if(");
        node.getExpression().accept(this);
        System.out.print(")");
        node.getStatement().accept(this);
    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        System.out.print("if(");
        node.getExpression().accept(this);
        System.out.print(")");
        node.getIfStatement().accept(this);
        System.out.print("else ");
        node.getElseStatement().accept(this);
    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {
        System.out.print("return ");
        if(node.getExpression() != null)
            node.getExpression().accept(this);
        System.out.println(";");
    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {
        System.out.println("break;");
    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {
        System.out.println("continue;");
    }

    @Override
    public void visit(ExpressionSemicolonStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        System.out.println(";");
    }


    @Override
    public void visit(ParameterDeclarationStatement node) throws ASTVisitorException {
        /*Variable parameter case*/
        if(node.getStructIdentifier().equals("") && !node.isArray()){
            System.out.print(node.getType().toString());
            System.out.print(" ");
            System.out.print(node.getIdentifier());
            System.out.print(",");
        }

        /*Variable array definition case*/
        if(node.getStructIdentifier().equals("") && node.isArray()){
            System.out.print(node.getType().toString());
            System.out.print("[");
            System.out.print("]");
            System.out.print(" ");
            System.out.print(node.getIdentifier());
            System.out.print(",");
        }

        /*Struct definition case*/
        if(!node.getStructIdentifier().equals("") && node.isArray() == false){
            System.out.print(node.getType().toString());
            System.out.print(" ");
            System.out.print(node.getStructIdentifier());
            System.out.print(" ");
            System.out.print(node.getIdentifier());
            System.out.print(",");
        }

        /*Struct array definition case*/
        if(!node.getStructIdentifier().equals("") && node.isArray() == true){
            System.out.print(node.getType().toString());
            System.out.print(" ");
            System.out.print(node.getStructIdentifier());
            System.out.print("[");
            System.out.print("]");
            System.out.print(" ");
            System.out.print(node.getIdentifier());
            System.out.print(",");
        }
    }

    @Override
    public void visit(FunctionDefinitionStatement node) throws ASTVisitorException {

        /*simple function definition*/
        if(node.getStructIdentifier().equals("")){
            System.out.print(node.getType());
            System.out.print(" ");
            System.out.print(node.getIdentifier());
            System.out.print(" ");
            System.out.print("(");
            for(ParameterDeclarationStatement p: node.getParameters())
                p.accept(this);
            System.out.print(")");
            System.out.println("{");
            //node.getStatementList().accept(this);
            for(Statement s : node.getStatements())
                s.accept(this);
            System.out.println("}");
        }else{
            System.out.print(node.getType());
            System.out.print(" ");
            System.out.print(node.getStructIdentifier());
            System.out.print(" ");
            System.out.print(node.getIdentifier());
            System.out.print(" ");
            System.out.print("(");
            for(ParameterDeclarationStatement p: node.getParameters())
                p.accept(this);
            System.out.print(")");
            System.out.println("{");
            //node.getStatementList().accept(this); //TODO ADD CURLY BRACES
            for(Statement s : node.getStatements())
                s.accept(this);
            System.out.println("}");
        }

    }

    @Override
    public void visit(IdentifierParenthesisExpression node) throws ASTVisitorException {
        System.out.print(node.getIdentifier());
        System.out.print("(");
        for(Expression e: node.getExpressions()) {
            e.accept(this);
            System.out.print(",");
        }
        System.out.print(")");

    }

    @Override
    public void visit(IdentifierBracketExpression node) throws ASTVisitorException {
        System.out.print(node.getIdentifier());
        System.out.print("[");
        node.getExpression().accept(this);
        System.out.print("]");
    }

    @Override
    public void visit(ExprDotIdentifierExpression node) throws ASTVisitorException {

        node.getExpression().accept(this);
        System.out.print(".");
        System.out.print(node.getIdentifier());

    }

    @Override
    public void visit(ExpressionDotArrayElementExpression node) throws ASTVisitorException{
        node.getExpression().accept(this);
        System.out.print(".");
        node.getIdentifierBracketExpression().accept(this);
    }

    @Override
    public void visit(VariableDefinition node) throws ASTVisitorException {
        System.out.print(node.getType());
        System.out.print(" ");
        System.out.print(node.getIdentifier());
        System.out.print(";\n");
    }

    @Override
    public void visit(VariableArrayDefinition node) throws ASTVisitorException {
        System.out.print(node.getType());
        System.out.print(" ");
        System.out.print(node.getIdentifier());
        System.out.print("[");
        System.out.print(node.getLiteral());
        System.out.print("]");
        System.out.println(";");
    }

    @Override
    public void visit(StructDeclarationStatement node) throws ASTVisitorException {
        System.out.print(node.getType());
        System.out.print(" ");
        System.out.print(node.getStructIdentifier());
        System.out.print(" ");
        System.out.print(node.getIdentifier());
        System.out.print(";\n");
    }

    @Override
    public void visit(StructArrayDeclaration node) throws ASTVisitorException {
        System.out.print(node.getType());
        System.out.print(" ");
        System.out.print(node.getStructIdentifier());
        System.out.print(" ");
        System.out.print(node.getIdentifier());
        System.out.print("[");
        System.out.print(node.getLiteral());
        System.out.print("]");
        System.out.println(";");
    }

    @Override
    public void visit(StructDefinitionStatement node) throws ASTVisitorException {
        System.out.print(node.getType());
        System.out.print(" ");
        System.out.print(node.getIdentifier());
        System.out.print("{\n");
        for(VariableDefinitionStatement varDef: node.getVariableDefinitions())varDef.accept(this);
        System.out.print("};\n");
    }


    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {
        System.out.println(" { ");
        for(Statement st: node.getStatements()) { 
            st.accept(this);
        }
        System.out.println(" } ");
    }

}
