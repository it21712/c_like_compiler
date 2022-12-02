import ast.*;
import org.objectweb.asm.Type;
import symbol.Info;
import symbol.LocalIndexPool;
import symbol.SimpleSymbolTable;
import symbol.StructInfo;

import java.util.ArrayList;

public class CollectSymbolsASTVisitor  implements ASTVisitor {


    public static final String ALREADY_DECLARED = " is already declared in this scope";
    public static final String NOT_DECLARED = " cannot be found";

    @Override
    public void visit(RootUnit node) throws ASTVisitorException {

        addPrintSymbol(node);

        for(CompUnit c : node.getCompUnits())
            c.accept(this);
    }

    @Override
    public void visit(CompUnit node) throws ASTVisitorException {
        for(Statement s : node.getStatements())
            s.accept(this);
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {

    }

    @Override
    public void visit(PrintStatement node) throws ASTVisitorException {

    }

    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {
        for(Statement s : node.getStatements())
            s.accept(this);
    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(IdentifierExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(CharLiteralExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(BooleanLiteralExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(CharacterLiteralExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        node.getStatement().accept(this);
    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        node.getStatement().accept(this);
    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        node.getIfStatement().accept(this);
        node.getElseStatement().accept(this);
    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {

        if(node.getExpression() != null) node.getExpression().accept(this);
    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {

    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {

    }

    @Override
    public void visit(ExpressionSemicolonStatement node) throws ASTVisitorException {

    }

    @Override
    public void visit(ParameterDeclarationStatement node) throws ASTVisitorException {
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);

        String structIdentifier = node.getStructIdentifier();
        String identifier = node.getIdentifier();

        if(symbolTable.lookupOnlyInTop(identifier) != null)
            ASTUtils.error(node, "Variable " + identifier + ALREADY_DECLARED);


        LocalIndexPool localIndexPool = ASTUtils.getSafeLocalIndexPool(node);
        Integer localIndex = localIndexPool.getLocalIndex(node.getType())-1; //TODO check
        if(structIdentifier.equals("")) symbolTable.put(identifier, new Info(identifier, node.getType(), localIndex/*structIdentifier*/));

        //if the parameter is a struct
        else {
            StructInfo structToken = (StructInfo) symbolTable.lookup(structIdentifier);
            if(structToken == null)
                ASTUtils.error(node, "Struct " + structIdentifier + NOT_DECLARED);
            symbolTable.put(identifier, new StructInfo(identifier, structIdentifier, node.getType(), structToken.getVariableDefinitions(), localIndex));
        }


    }

    @Override
    public void visit(FunctionDefinitionStatement node) throws ASTVisitorException {

        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        String identifier = node.getIdentifier();
        Type funType = node.getType();

        Info token = symbolTable.lookup(identifier);

        if( token != null) {

            if (token.isFunction())
                ASTUtils.error(node, "Function " + identifier + ALREADY_DECLARED);
        }


        for(ParameterDeclarationStatement ps : node.getParameters())
            ps.accept(this);
        for(Statement s : node.getStatements())
            s.accept(this);


        symbolTable.put(identifier, new Info(identifier, funType, true));
    }

    @Override
    public void visit(IdentifierParenthesisExpression node) throws ASTVisitorException {
    }

    @Override
    public void visit(IdentifierBracketExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(ExprDotIdentifierExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(ExpressionDotArrayElementExpression node) throws ASTVisitorException {

    }

    @Override
    public void visit(VariableDefinition node) throws ASTVisitorException {
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        String identifier = node.getIdentifier();
        String structId = ASTUtils.getStructNameProperty(node);
        if(structId == null) structId = "";
        if(symbolTable.lookupOnlyInTop(identifier) != null)
            ASTUtils.error(node, "Variable " + identifier + ALREADY_DECLARED);


        LocalIndexPool localIndexPool = ASTUtils.getSafeLocalIndexPool(node);
        Integer localIndex = localIndexPool.getLocalIndex(node.getType());
        Info info = new Info(identifier, node.getType(), localIndex);
        info.setStructId(structId);

        symbolTable.put(identifier, info);

    }

    @Override
    public void visit(VariableArrayDefinition node) throws ASTVisitorException {
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        String identifier = node.getIdentifier();
        String structId = ASTUtils.getStructNameProperty(node);
        if(structId == null) structId = "";

        if(symbolTable.lookupOnlyInTop(identifier) != null)
            ASTUtils.error(node, "Variable " + identifier + ALREADY_DECLARED);

        LocalIndexPool localIndexPool = ASTUtils.getSafeLocalIndexPool(node);
        Integer localIndex = localIndexPool.getLocalIndex(node.getType());
        Info info = new Info(identifier, node.getType(), node.getLiteral(), localIndex);
        info.setStructId(structId);
        symbolTable.put(identifier, info);
    }

    @Override
    public void visit(StructDeclarationStatement node) throws ASTVisitorException {
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        String identifier = node.getIdentifier();
        String structIdentifier = node.getStructIdentifier();


        StructInfo structToken = (StructInfo) symbolTable.lookup(structIdentifier);

        if(symbolTable.lookupOnlyInTop(identifier) != null)
            ASTUtils.error(node, "Variable " + identifier + ALREADY_DECLARED);
        if(structToken == null)
            ASTUtils.error(node, "Struct " + structIdentifier + NOT_DECLARED);

        //symbolTable.put(identifier, new Info(identifier, node.getType()));
        LocalIndexPool localIndexPool = ASTUtils.getSafeLocalIndexPool(node);
        Integer localIndex = localIndexPool.getLocalIndex(Type.getType("Ljava/lang/Object"));
        StructInfo structInfo = new StructInfo(identifier, structIdentifier, node.getType(), structToken.getVariableDefinitions(), localIndex);
        //structInfo.setFieldOf(ASTUtils.getStructNameProperty(node));
        structInfo.setFieldOf(ASTUtils.getStructNameProperty(node) == null ? "" : ASTUtils.getStructNameProperty(node));
        symbolTable.put(identifier, structInfo);

    }

    @Override
    public void visit(StructArrayDeclaration node) throws ASTVisitorException {
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        String identifier = node.getIdentifier();
        String structIdentifier = node.getStructIdentifier();


        StructInfo structToken = (StructInfo) symbolTable.lookup(structIdentifier);



        if(symbolTable.lookupOnlyInTop(identifier) != null)
            ASTUtils.error(node, "Variable " + identifier + ALREADY_DECLARED);
        if(structToken == null)
            ASTUtils.error(node, "Struct " + structIdentifier + NOT_DECLARED);

        LocalIndexPool localIndexPool = ASTUtils.getSafeLocalIndexPool(node);
        Integer localIndex = localIndexPool.getLocalIndex(Type.getType("Ljava/lang/Object"));

        //symbolTable.put(identifier, new Info(identifier, node.getType()));
        StructInfo structInfo = new StructInfo(identifier, structIdentifier, node.getType(), node.getLiteral(), structToken.getVariableDefinitions(), localIndex);
        //structInfo.setFieldOf(ASTUtils.getStructNameProperty(node));
        structInfo.setFieldOf(ASTUtils.getStructNameProperty(node) == null ? "" : ASTUtils.getStructNameProperty(node));
        symbolTable.put(identifier, structInfo);
    }

    @Override
    public void visit(StructDefinitionStatement node) throws ASTVisitorException {

        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        String identifier = node.getIdentifier();

        ArrayList<Info> variableDefTokens = new ArrayList<>();

        for(VariableDefinitionStatement vs : node.getVariableDefinitions()) {

            ASTUtils.setStructFieldProperty(vs, node.getIdentifier());
            ASTUtils.setFieldProperty(vs, true);
            vs.accept(this);
            Info variableDefToken = ASTUtils.getSafeSymbolTable(vs).lookup(vs.getId());
            symbolTable.put(variableDefToken.getId(), variableDefToken);

            variableDefTokens.add(variableDefToken);

        }


        if(symbolTable.lookupOnlyInTop(identifier) != null)
            ASTUtils.error(node, "Struct " + identifier + ALREADY_DECLARED);


        //symbolTable.put(identifier, new StructInfo(identifier, Type.getType("Ljava/lang/Object;"), variableDefTokens));
        symbolTable.put(identifier, new StructInfo(identifier, node.getType().toString(), variableDefTokens));
    }

    private void addPrintSymbol(RootUnit node) throws ASTVisitorException {
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        String identifier = "print";
        Type returnType = Type.VOID_TYPE;
        Type intParamType = Type.INT_TYPE;
        Type floatParamType = Type.FLOAT_TYPE;
        Type charParamType = Type.CHAR_TYPE;
        Type boolParamType = Type.BOOLEAN_TYPE;
        Type stringParamType = Type.getType(String.class);
        Type objectParamType = Type.getType(Object.class);

        Type printMethodIntType = Type.getMethodType(returnType, intParamType);
        Type printMethodFloatType = Type.getMethodType(returnType, floatParamType);
        Type printMethodCharType = Type.getMethodType(returnType, charParamType);
        Type printMethodBoolType = Type.getMethodType(returnType, boolParamType);
        Type printMethodStringType = Type.getMethodType(returnType, stringParamType);
        Type printMethodObjectType = Type.getMethodType(returnType, objectParamType);

        ArrayList<Type> printTypes = new ArrayList<>();
        printTypes.add(printMethodIntType);
        printTypes.add(printMethodFloatType);
        printTypes.add(printMethodCharType);
        printTypes.add(printMethodBoolType);
        printTypes.add(printMethodStringType);
        printTypes.add(printMethodObjectType);

        Info info = new Info(identifier, printTypes, true);
        symbolTable.put(identifier, info);
    }
}
