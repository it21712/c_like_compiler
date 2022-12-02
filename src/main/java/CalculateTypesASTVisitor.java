import ast.*;
import org.objectweb.asm.Type;
import symbol.Info;
import symbol.SimpleSymbolTable;
import symbol.StructInfo;
import types.TypeException;
import types.TypeUtils;

import java.util.ArrayList;

public class CalculateTypesASTVisitor implements ASTVisitor {

    private static final String UNDECLARED_IDENTIFIER = "Undeclared Identifier ";

    @Override
    public void visit(RootUnit node) throws ASTVisitorException {
        for(CompUnit c : node.getCompUnits())
            c.accept(this);


        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        Info funMain = symbolTable.lookup("main");
        if(funMain == null) ASTUtils.error(node, "There needs to be a main function in order to compile the program");

    }

    @Override
    public void visit(CompUnit node) throws ASTVisitorException {
        for(Statement s : node.getStatements())
            s.accept(this);
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {
        if(!(node.getExpression0() instanceof IdentifierExpression) && !(node.getExpression0() instanceof IdentifierBracketExpression) &&
                !(node.getExpression0() instanceof ExprDotIdentifierExpression) && !(node.getExpression0() instanceof ExpressionDotArrayElementExpression))
            ASTUtils.error(node.getExpression0(), "Left side expression cannot be " + node.getExpression0().getClass().getCanonicalName());
        node.getExpression0().accept(this);
        node.getExpression().accept(this);

        Type leftType = ASTUtils.getSafeType(node.getExpression0());
        Type rightType = ASTUtils.getSafeType(node.getExpression());

        if(!TypeUtils.isAssignable(leftType, rightType))
            ASTUtils.error(node, "Cannot assign type " + rightType + " to " + leftType);

        ASTUtils.setType(node, leftType);
    }

    @Override
    public void visit(PrintStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {

        boolean isUnreachable = false;

        int stmtCount = node.getStatements().size();
        int returns = 1;

        for(int s = 0; s < stmtCount; s++){
            Statement statement = node.getStatements().get(s);

            statement.setInLoop(node.isInLoop());
            statement.setParentType(node.getParentType());
            statement.accept(this);

            if(isUnreachable) ASTUtils.error(statement, "unreachable statement");

            if(statement.alwaysReturns()){
                isUnreachable = true;
                node.setAlwaysReturns(true);
                node.setReturnsValue(true);
                returns = 0;
                if(TypeUtils.isLargerType(ASTUtils.getSafeType(statement), node.getParentType())) ASTUtils.error(statement, "Wrong type");
            }

        }

        if(returns !=0){
            ASTUtils.setType(node, Type.VOID_TYPE);
            node.setReturnsValue(false);
        }

        //todo change??
        ASTUtils.setType(node, node.getParentType());

    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        node.getExpression2().accept(this);

        Type type1 = ASTUtils.getSafeType(node.getExpression1());
        Type type2 = ASTUtils.getSafeType(node.getExpression2());

        Operator operator = node.getOperator();

        try {
            Type exprType = TypeUtils.applyBinary(operator, type1, type2);
            ASTUtils.setType(node, exprType);
        } catch (TypeException e) {
            ASTUtils.error(node, e.getMessage());
        }
    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
        Operator operator = node.getOperator();
        try{
            ASTUtils.setType(node, TypeUtils.applyUnary(operator, ASTUtils.getSafeType(node.getExpression())));
        }catch (TypeException e){
            ASTUtils.error(node, e.getMessage());
        }
    }

    @Override
    public void visit(IdentifierExpression node) throws ASTVisitorException {
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);

        Info info = symbolTable.lookup(node.getIdentifier());
        if(info == null)
            ASTUtils.error(node, UNDECLARED_IDENTIFIER + node.getIdentifier());

        Type idType = (Type) info.getValue();
        ASTUtils.setType(node, idType);

    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.FLOAT_TYPE);
    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.INT_TYPE);
    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.getType(String.class));
    }

    @Override
    public void visit(CharLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.CHAR_TYPE);
    }

    @Override
    public void visit(BooleanLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.BOOLEAN_TYPE);
    }

    @Override
    public void visit(CharacterLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.CHAR_TYPE);
    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);

        Type exprType = ASTUtils.getSafeType(node.getExpression());

        ASTUtils.setType(node, exprType);
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);

        Type exprType = ASTUtils.getSafeType(node.getExpression());

        if(!TypeUtils.isLogical(exprType))
            ASTUtils.error(node, "While statement expression must be logical");

        node.getStatement().setInLoop(true);
        node.getStatement().setParentType(node.getParentType());
        node.getStatement().accept(this);

        if(node.getStatement().returnsValue()){
            if (TypeUtils.isLargerType(ASTUtils.getSafeType(node.getStatement()), node.getParentType()))
                ASTUtils.error(node.getStatement(), "Wrong return type");
        }

        ASTUtils.setType(node, ASTUtils.getSafeType(node.getStatement()));

    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        Type exprType = ASTUtils.getSafeType(node.getExpression());

        if(!TypeUtils.isLogical(exprType))
            ASTUtils.error(node, "If statement expression must be logical");

        node.getStatement().setInLoop(node.isInLoop());
        node.getStatement().setParentType(node.getParentType());
        node.getStatement().accept(this);


        if(node.getStatement().returnsValue()) {
            if (TypeUtils.isLargerType(ASTUtils.getSafeType(node.getStatement()), node.getParentType()))
                ASTUtils.error(node.getStatement(), "Wrong return type");
        }

        ASTUtils.setType(node, ASTUtils.getSafeType(node.getStatement()));


    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);

        Type exprType = ASTUtils.getSafeType(node.getExpression());

        if(!TypeUtils.isLogical(exprType))
            ASTUtils.error(node, "If statement expression must be logical");

        node.getIfStatement().setInLoop(node.isInLoop());
        node.getIfStatement().setParentType(node.getParentType());
        node.getIfStatement().accept(this);
        node.getElseStatement().setInLoop(node.isInLoop());
        node.getElseStatement().setParentType(node.getParentType());
        node.getElseStatement().accept(this);

        node.setAlwaysReturns(node.getIfStatement().alwaysReturns() && node.getElseStatement().alwaysReturns());
        ASTUtils.setType(node, node.getParentType());



    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {

        Type exprType = Type.VOID_TYPE;

        Expression expr = node.getExpression();
        if(expr != null) {
            expr.accept(this);
            exprType = ASTUtils.getSafeType(expr);
        }

        ASTUtils.setType(node, exprType);
        node.setReturnsValue(true);
        node.setAlwaysReturns(true);


    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {

        if(!node.isInLoop())
            ASTUtils.error(node, "Break statement must only be used inside a loop");

        ASTUtils.setType(node, Type.VOID_TYPE);

    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {

        if(!node.isInLoop())
            ASTUtils.error(node, "Continue statement must only be used inside a loop");

        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(ExpressionSemicolonStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);

        ASTUtils.setType(node, ASTUtils.getSafeType(node.getExpression()));
    }

    @Override
    public void visit(ParameterDeclarationStatement node) throws ASTVisitorException {
        ASTUtils.setType(node, node.getType());
    }
    //TODO add arrays as return types
    @Override
    public void visit(FunctionDefinitionStatement node) throws ASTVisitorException {


        boolean isUnreachable = false;

        int stmtCount = node.getStatements().size();

        int returns = 1;

        Type methodReturnType = node.getType().getReturnType();
        Type r = Type.VOID_TYPE;

        for(int s = 0; s < stmtCount; s++){
            Statement statement = node.getStatements().get(s);
            statement.setParentType(methodReturnType);
            statement.accept(this);

            if(isUnreachable) ASTUtils.error(statement, "Unreachable statement");

            if(statement.alwaysReturns()){
                isUnreachable = true;
                r = ASTUtils.getSafeType(statement);
                returns = 0;
            }

        }
        if(methodReturnType.equals(Type.VOID_TYPE)) returns = 0;

        if(returns != 0) ASTUtils.error(node, "Not all code paths return a value");
        if(TypeUtils.isLargerType(r, methodReturnType)) ASTUtils.error(node, "Method " + node.getIdentifier()+ " returns " + methodReturnType + " but the return value is " + r);

        ASTUtils.setType(node, methodReturnType);

    }

    @Override
    public void visit(IdentifierParenthesisExpression node) throws ASTVisitorException {

        Type[] parameterTypes = new Type[node.getExpressions().size()];

        int paramCount = node.getExpressions().size();

        for(int e = 0; e < paramCount; e++) {
            Expression expr = node.getExpressions().get(e);
            expr.accept(this);
            parameterTypes[e]= ASTUtils.getSafeType(expr);
        }

        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);

        /*Method print from our standard library*/
        if(node.getIdentifier().equals("print")){
            Info printInfo = symbolTable.lookup(node.getIdentifier());
            ArrayList<Type> printTypes = (ArrayList<Type>) printInfo.getValue();


            //check parameter count
            if(paramCount != 1)
                ASTUtils.error(node, "Expected 1 argument but got " + parameterTypes.length);

            for(Type t : printTypes) {
                if (t.getArgumentTypes()[0].equals(parameterTypes[0])) {

                    ASTUtils.setType(node, t);

                    return;
                }
            }

            ASTUtils.error(node, "Given type is not supported by print");

        }

        /*all other user defined method calls*/
        else{

            Info funInfo = symbolTable.lookup(node.getIdentifier());
            if(funInfo == null) ASTUtils.error(node, "Method " + node.getIdentifier()+ " cannot be found");

            Type funType = (Type)funInfo.getValue();

            Type[] funParameterTypes = funType.getArgumentTypes();


            if(funParameterTypes.length != parameterTypes.length) ASTUtils.error(node,
                    "Expected " + funParameterTypes.length + " arguments but got " + parameterTypes.length);

            for(int t = 0; t < funParameterTypes.length; t++){
                if(TypeUtils.isLargerType(parameterTypes[t], funParameterTypes[t])) ASTUtils.error(node,
                        "Cannot assign " + parameterTypes[t] + " to " + funParameterTypes[t]);
            }


            ASTUtils.setType(node, funType.getReturnType());
        }



    }

    @Override
    public void visit(IdentifierBracketExpression node) throws ASTVisitorException {
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);

        Info token = symbolTable.lookup(node.getIdentifier());
        if(token != null){
            node.getExpression().accept(this);

            Type exprType = (Type) token.getValue();
            exprType = exprType.getElementType();
            ASTUtils.setType(node, exprType);
        }

        else ASTUtils.error(node, UNDECLARED_IDENTIFIER + node.getIdentifier());
    }


    /*@Override
    public void visit(ExprDotIdentifierExpression node) throws ASTVisitorException {
        if(node.getExpression() instanceof IdentifierExpression){
            node.getExpression().accept(this);

            SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
            String tokenId = ((IdentifierExpression) node.getExpression()).getIdentifier();
            Info token = symbolTable.lookup(tokenId);


            //if identifier is a struct, search for the field and set node type to field type
            if(token instanceof StructInfo){
                StructInfo structToken = (StructInfo)token;

                Info structField = structToken.containsVariable(node.getIdentifier());
                if(structField == null)
                    ASTUtils.error(node, "Struct " + structToken.getId() + " does not contain a variable called " + node.getIdentifier());

                ASTUtils.setType(node, (Type) structField.getValue());

            }
            //if identifier is a simple variable, throw error
            else ASTUtils.error(node, tokenId+" does not contain a variable called " + node.getIdentifier());


        }else if (node.getExpression() instanceof IdentifierBracketExpression){
            node.getExpression().accept(this);

            SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
            String tokenId = ((IdentifierBracketExpression) node.getExpression()).getIdentifier();
            Info token = symbolTable.lookup(tokenId);


            //if identifier is a struct, search for the field and set node type to field type
            if(token instanceof StructInfo){
                StructInfo structToken = (StructInfo)token;

                Info structField = structToken.containsVariable(node.getIdentifier());
                if(structField == null)
                    ASTUtils.error(node, "Struct " + structToken.getId() + " does not contain a variable called " + node.getIdentifier());

                ASTUtils.setType(node, (Type) structField.getValue());

            }
            //if identifier is a simple variable, throw error
            else ASTUtils.error(node, tokenId+" does not contain a variable called " + node.getIdentifier());
        }else if(node.getExpression() instanceof ExprDotIdentifierExpression){
            node.getExpression().accept(this);

            ExprDotIdentifierExpression expr = (ExprDotIdentifierExpression) node.getExpression();

            String tokenId = expr.getIdentifier();
            SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
            Info token = symbolTable.lookup(tokenId);

            //if identifier is a struct, search for the field and set node type to field type
            if(token instanceof StructInfo){
                StructInfo structToken = (StructInfo)token;

                Info structField = structToken.containsVariable(node.getIdentifier());
                if(structField == null)
                    ASTUtils.error(node, "Struct " + structToken.getId() + " does not contain a variable called " + node.getIdentifier());

                ASTUtils.setType(node, (Type) structField.getValue());
                ASTUtils.setType(node.getExpression(), (Type) structToken.getValue());
            }
            //if identifier is a simple variable, throw error
            else ASTUtils.error(node, tokenId+" does not contain a variable called " + node.getIdentifier());


        }else{
            ASTUtils.error(node, "Invalid syntax");
        }

    }*/

    @Override
    public void visit(ExprDotIdentifierExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);

        Type exprType = ASTUtils.getSafeType(node.getExpression());

        if(!exprType.getDescriptor().startsWith("L")) ASTUtils.error(node,
                "Cannot find field " + node.getIdentifier());
        //get struct id
        String structName = exprType.getClassName();

        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);

        StructInfo structInfo = (StructInfo) symbolTable.lookup(structName);

        if(structInfo == null) ASTUtils.error(node, "struct " + structName+" cannot be found");

        Info fieldInfo = structInfo.containsVariable(node.getIdentifier());
        if(fieldInfo == null) ASTUtils.error(node, "struct " + structName
                + " does not contain a variable called " + node.getIdentifier());

        ASTUtils.setType(node, (Type) fieldInfo.getValue());

    }



    @Override
    public void visit(ExpressionDotArrayElementExpression node) throws ASTVisitorException{
        node.getExpression().accept(this);

        Type exprType = ASTUtils.getSafeType(node.getExpression());
        String arrayId = node.getIdentifierBracketExpression().getIdentifier();
        if(!exprType.getDescriptor().startsWith("L")) ASTUtils.error(node,
                "Cannot find array field " + arrayId);

        //get struct id
        String structName = exprType.getClassName();

        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);

        StructInfo structInfo = (StructInfo) symbolTable.lookup(structName);

        if(structInfo == null) ASTUtils.error(node, "struct " + structName + " cannot be found");


        Info fieldInfo = structInfo.containsVariable(arrayId);
        if(fieldInfo == null) ASTUtils.error(node, "struct " + structName
                + " does not contain an array called " + arrayId);


        ASTUtils.setType(node, ((Type) fieldInfo.getValue()).getElementType());

    }

    @Override
    public void visit(VariableDefinition node) throws ASTVisitorException {
        if(node.getType().equals(Type.VOID_TYPE)) ASTUtils.error(node, "Variables cannot be of type VOID");
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(VariableArrayDefinition node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(StructDeclarationStatement node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(StructArrayDeclaration node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(StructDefinitionStatement node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.VOID_TYPE);
    }
}
