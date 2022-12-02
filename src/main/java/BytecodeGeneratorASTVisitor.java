import ast.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import symbol.Info;
import symbol.SimpleSymbolTable;
import symbol.StructInfo;
import types.TypeUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ast.Operator;


public class BytecodeGeneratorASTVisitor implements ASTVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BytecodeGeneratorASTVisitor.class);

    private ClassNode cn;
    private MethodNode mn;

    public ReloadingClassLoader cl;

    public BytecodeGeneratorASTVisitor() {

        cl = new ReloadingClassLoader(ClassLoader.getSystemClassLoader());

        // create class
        cn = new ClassNode();
        cn.access = Opcodes.ACC_PUBLIC;
        cn.version = Opcodes.V1_5;
        cn.name = "Program";
        cn.sourceFile = "Program.in";
        cn.superName = "java/lang/Object";

        // create constructor
        mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        mn.maxLocals = 1;
        mn.maxStack = 1;
        cn.methods.add(mn);
    }

    public ClassNode getClassNode() {
        return cn;
    }


    @Override
    public void visit(CompUnit node) throws ASTVisitorException {

        Statement s = null, ps;

        Iterator<Statement> it = node.getStatements().iterator();
        while (it.hasNext()){
            ps = s;
            s = it.next();

            if (ps != null && !ASTUtils.getNextList(ps).isEmpty()) {
                LabelNode labelNode = new LabelNode();
                mn.instructions.add(labelNode);
                backPatch(ASTUtils.getNextList(ps), labelNode);
            }

            s.accept(this);


        }
        if (s != null && !ASTUtils.getNextList(s).isEmpty()) {
            LabelNode labelNode = new LabelNode();
            mn.instructions.add(labelNode);
            backPatch(ASTUtils.getNextList(s), labelNode);
        }

    }

    @Override
    public void visit(RootUnit node) throws ASTVisitorException {
        for(CompUnit c : node.getCompUnits()) c.accept(this);
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException{

        ASTUtils.setLeftSideExpressionProperty(node.getExpression0(), true);

        if(node.getExpression0() instanceof ExprDotIdentifierExpression){
            node.getExpression0().accept(this);
            node.getExpression().accept(this);
            //putfield

            ExprDotIdentifierExpression expr = (ExprDotIdentifierExpression) node.getExpression0();
            String fieldId = expr.getIdentifier();

            String structName = ASTUtils.getSafeType(expr.getExpression()).getClassName();

            Info fieldInfo = ((StructInfo)ASTUtils.getSafeSymbolTable(expr).lookup(structName)).containsVariable(fieldId);
            //if(fieldInfo instanceof StructInfo) structId = ((StructInfo)fieldInfo).getFieldOf();

            mn.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD,structName,
                    fieldId, ((Type)fieldInfo.getValue()).toString()));

        }else if(node.getExpression0() instanceof ExpressionDotArrayElementExpression){
            node.getExpression0().accept(this);
            node.getExpression().accept(this);
            //store array element

            ExpressionDotArrayElementExpression expr = (ExpressionDotArrayElementExpression) node.getExpression0();

            Type fieldType = ASTUtils.getSafeType(node.getExpression0());

            mn.instructions.add(new InsnNode(fieldType.getOpcode(Opcodes.IASTORE)));
        }else if(node.getExpression0() instanceof IdentifierBracketExpression){
            Type fieldType = ASTUtils.getSafeType(node.getExpression0());
            node.getExpression0().accept(this);
            node.getExpression().accept(this);
            mn.instructions.add(new InsnNode(fieldType.getOpcode(Opcodes.IASTORE)));
        }else{
            node.getExpression().accept(this);
            node.getExpression0().accept(this);


        }



    }

    @Override
    public void visit(PrintStatement node) throws ASTVisitorException {
    }

    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {
        List<JumpInsnNode> breakList = new ArrayList<>();
        List<JumpInsnNode> continueList = new ArrayList<>();

        Statement s = null, ps;

        Iterator<Statement> it = node.getStatements().iterator();

        while(it.hasNext()){
            ps = s;
            s = it.next();
            if(ps != null && !ASTUtils.getNextList(ps).isEmpty()) {
                LabelNode labelNode = new LabelNode();
                mn.instructions.add(labelNode);
                backPatch(ASTUtils.getNextList(ps), labelNode);
            }

            s.accept(this);

            breakList.addAll(ASTUtils.getBreakList(s));
            continueList.addAll(ASTUtils.getContinueList(s));
        }
        if(s != null) ASTUtils.setNextList(node, ASTUtils.getNextList(s));

        ASTUtils.setBreakList(node, breakList);
        ASTUtils.setContinueList(node, continueList);
    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {

        Type expr1Type = ASTUtils.getSafeType(node.getExpression1());
        Type expr2Type = ASTUtils.getSafeType(node.getExpression2());

        Type maxType = TypeUtils.maxType(expr1Type, expr2Type);
        if(ASTUtils.isBooleanExpression(node)){

            if(maxType.equals(Type.BOOLEAN_TYPE)){
                handleLogicalOperations(node, node.getOperator());

            }else {
                node.getExpression1().accept(this);
                widen(maxType, expr1Type);
                node.getExpression2().accept(this);
                widen(maxType, expr2Type);
                handleBooleanOperator(node, node.getOperator(), maxType);

            }
        }else{

            if(maxType.equals(Type.BOOLEAN_TYPE)){
                calculateBooleanBinary(node, node.getOperator(), maxType);
                return;
            }
            node.getExpression1().accept(this);
            widen(maxType, expr1Type);
            node.getExpression2().accept(this);
            widen(maxType, expr2Type);

            handleNonBooleanBinary(node, node.getOperator(), maxType);
        }


    }


    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);

        Type type = ASTUtils.getSafeType(node.getExpression());

        if (node.getOperator().equals(Operator.MINUS)) {
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.INEG)));
        } else if(node.getOperator().equals(Operator.NOT)){


            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.T_BOOLEAN)));//NOT OPCODE??
            //throw new ASTVisitorException("Unsupported operator " + node.getOperator());
        }

    }


    @Override
    public void visit(IdentifierExpression node) throws ASTVisitorException{
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        Info tokenInfo = symbolTable.lookup(node.getIdentifier());
        //Type tokenType = ASTUtils.getSafeType(node);
        Type tokenType = (Type) tokenInfo.getValue();
        Integer localIndex = tokenInfo.getIndex();
        if(!ASTUtils.getLeftSideExpressionProperty(node)){

            mn.instructions.add(new VarInsnNode(tokenType.getOpcode(Opcodes.ILOAD), localIndex));


            return;
        }

        mn.instructions.add(new VarInsnNode(tokenType.getOpcode(Opcodes.ISTORE), localIndex));

    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        Float value = node.getLiteral();
        mn.instructions.add(new LdcInsnNode(value));
    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        Integer value = node.getLiteral();
        mn.instructions.add(new LdcInsnNode(value));
    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        String value = node.getLiteral();
        mn.instructions.add(new LdcInsnNode(value));
    }

    @Override
    public void visit(CharLiteralExpression node) throws ASTVisitorException {
        Character value = node.getLiteral();
        mn.instructions.add(new LdcInsnNode(value));
    }

    @Override
    public void visit(BooleanLiteralExpression node) throws ASTVisitorException {
        if(ASTUtils.isBooleanExpression(node)){
            JumpInsnNode i = new JumpInsnNode(Opcodes.GOTO, null);
            mn.instructions.add(i);

            if(node.getLiteral()) ASTUtils.getTrueList(node).add(i);
            else ASTUtils.getFalseList(node).add(i);

        }else{
            boolean value = node.getLiteral();
            mn.instructions.add(new InsnNode(value? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        }
    }

    @Override
    public void visit(CharacterLiteralExpression node) throws ASTVisitorException {

        Character value = node.getLiteral();

        mn.instructions.add(new LdcInsnNode(value));

    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        ASTUtils.setBooleanExpression(node.getExpression(), true);

        LabelNode beginLabelNode = new LabelNode();
        mn.instructions.add(beginLabelNode);

        node.getExpression().accept(this);

        LabelNode trueLabelNode = new LabelNode();
        mn.instructions.add(trueLabelNode);
        backPatch(ASTUtils.getTrueList(node.getExpression()), trueLabelNode);

        node.getStatement().accept(this);

        backPatch(ASTUtils.getNextList(node.getStatement()), beginLabelNode);
        backPatch(ASTUtils.getContinueList(node.getStatement()), beginLabelNode);

        mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, beginLabelNode));

        ASTUtils.getNextList(node).addAll(ASTUtils.getFalseList(node.getExpression()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getBreakList(node.getStatement()));

    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        ASTUtils.setBooleanExpression(node.getExpression(), true);

        node.getExpression().accept(this);

        LabelNode labelNode = new LabelNode();
        mn.instructions.add(labelNode);
        backPatch(ASTUtils.getTrueList(node.getExpression()), labelNode);

        node.getStatement().accept(this);

        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getStatement()));
        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getStatement()));

        ASTUtils.getNextList(node).addAll(ASTUtils.getFalseList(node.getExpression()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getStatement()));

    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        ASTUtils.setBooleanExpression(node.getExpression(), true);

        node.getExpression().accept(this);

        LabelNode stmt1StartLabelNode = new LabelNode();
        mn.instructions.add(stmt1StartLabelNode);
        node.getIfStatement().accept(this);

        JumpInsnNode skipGoto = new JumpInsnNode(Opcodes.GOTO, null);
        mn.instructions.add(skipGoto);

        LabelNode stmt2StartLabelNode = new LabelNode();
        mn.instructions.add(stmt2StartLabelNode);
        node.getElseStatement().accept(this);

        backPatch(ASTUtils.getTrueList(node.getExpression()), stmt1StartLabelNode);
        backPatch(ASTUtils.getFalseList(node.getExpression()), stmt2StartLabelNode);

        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getIfStatement()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getElseStatement()));
        ASTUtils.getNextList(node).add(skipGoto);

        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getIfStatement()));
        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getElseStatement()));

        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getIfStatement()));
        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getElseStatement()));

        ASTUtils.getNextList(node).add(skipGoto);

    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {
        if(node.getExpression() != null) {
            node.getExpression().accept(this);
            mn.instructions.add(new InsnNode(ASTUtils.getSafeType(node.getExpression()).getOpcode(Opcodes.IRETURN)));
            return;
        }

        mn.instructions.add(new InsnNode(Opcodes.RETURN));

    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {
        JumpInsnNode jmp = new JumpInsnNode(Opcodes.GOTO, null);
        mn.instructions.add(jmp);
        ASTUtils.getBreakList(node).add(jmp);

    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {
        JumpInsnNode jmp = new JumpInsnNode(Opcodes.GOTO, null);
        mn.instructions.add(jmp);
        ASTUtils.getContinueList(node).add(jmp);

    }

    @Override
    public void visit(ExpressionSemicolonStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(ParameterDeclarationStatement node) throws ASTVisitorException {
        Info paramInfo = ASTUtils.getSafeSymbolTable(node).lookupOnlyInTop(node.getIdentifier());


    }

    @Override
    public void visit(FunctionDefinitionStatement node) throws ASTVisitorException {

        if(node.getIdentifier().equals("main")) {
            mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "()V", null, null);

        }
        else mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, node.getIdentifier(), node.getType().toString(), null, null);

        for(ParameterDeclarationStatement pds : node.getParameters()) {
            pds.accept(this);
        }
        Statement s = null, ps;

        Iterator<Statement> it = node.getStatements().iterator();

        while (it.hasNext()){
            ps = s;
            s = it.next();

            if(ps != null && !ASTUtils.getNextList(ps).isEmpty()) {
                LabelNode labelNode = new LabelNode();
                mn.instructions.add(labelNode);
                backPatch(ASTUtils.getNextList(ps), labelNode);
            }

            s.accept(this);
        }

        if(s != null && !ASTUtils.getNextList(s).isEmpty()) {
            LabelNode labelNode = new LabelNode();
            mn.instructions.add(labelNode);
            backPatch(ASTUtils.getNextList(s), labelNode);
        }

        cn.methods.add(mn);
    }


    //Function call
    @Override
    public void visit(IdentifierParenthesisExpression node) throws ASTVisitorException {

        /*print method from our standard lib*/
        if(node.getIdentifier().equals("print")){
            mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
            node.getExpressions().get(0).accept(this); //The previous visitor (Calculate Types) ensures there is only one parameter in the print method else throws error
            Type printType = ASTUtils.getSafeType(node);

            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", printType.toString()));
        }else{
            SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
            Info funInfo = symbolTable.lookup(node.getIdentifier());
            Type type = (Type) funInfo.getValue(); //we get the full method descriptor, because ASTUtils.getSafeType gives the method's return type only


            for(Expression e : node.getExpressions())
                e.accept(this);

            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getClassNode().name, node.getIdentifier(), type.toString()));
        }
    }

    @Override
    public void visit(IdentifierBracketExpression node) throws ASTVisitorException{
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        Info info = symbolTable.lookup(node.getIdentifier());

        Type type = ASTUtils.getSafeType(node);

        Integer localIndex = info.getIndex();

        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, localIndex));
        node.getExpression().accept(this);

        if(!ASTUtils.getLeftSideExpressionProperty(node)) {
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.IALOAD)));
        }
    }


    @Override
    public void visit(ExprDotIdentifierExpression node) throws ASTVisitorException{
        node.getExpression().accept(this);

        String structName = ASTUtils.getSafeType(node.getExpression()).getClassName();

        String fieldId = node.getIdentifier();
        Type fieldType = ASTUtils.getSafeType(node);
        if(!ASTUtils.getLeftSideExpressionProperty(node)){
            mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,structName,
                    fieldId, fieldType.toString()));

        }

    }


    @Override
    public void visit(ExpressionDotArrayElementExpression node) throws ASTVisitorException{
        node.getExpression().accept(this);

        String fieldId = node.getIdentifierBracketExpression().getIdentifier();

        String structName = ASTUtils.getSafeType(node.getExpression()).getClassName();


        Type fieldType = ASTUtils.getSafeType(node);

        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,structName,
                fieldId, "["+fieldType.toString()));


        node.getIdentifierBracketExpression().getExpression().accept(this);

        if(!ASTUtils.getLeftSideExpressionProperty(node)){
            mn.instructions.add(new InsnNode(fieldType.getOpcode(Opcodes.IALOAD)));
        }


    }

    @Override
    public void visit(VariableDefinition node) throws ASTVisitorException {

    }
    //TODO REMOVE VOID FROM VARIABLE TYPES
    @Override
    public void visit(VariableArrayDefinition node) throws ASTVisitorException {
        Type type = node.getType();

        SimpleSymbolTable symbolTable = ASTUtils.getSafeSymbolTable(node);
        Info token = symbolTable.lookup(node.getIdentifier());
        Integer index = token.getIndex();
        mn.instructions.add(new LdcInsnNode(node.getLiteral()));
        mn.instructions.add(new IntInsnNode(Opcodes.NEWARRAY, TypeUtils.getTypeCode(type.getElementType())));
        mn.instructions.add(new VarInsnNode(type.getOpcode(Opcodes.ISTORE), index));
    }

    @Override
    public void visit(StructDeclarationStatement node) throws ASTVisitorException {

        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, node.getStructIdentifier()));
        mn.instructions.add(new InsnNode(Opcodes.DUP));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, node.getStructIdentifier(), "<init>", "()V"));
        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        StructInfo info = (StructInfo) symbolTable.lookup(node.getIdentifier());
        Integer index = info.getIndex();

        mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, index));

    }

    @Override
    public void visit(StructArrayDeclaration node) throws ASTVisitorException {
        Type type = node.getType();
        SimpleSymbolTable symbolTable = ASTUtils.getSafeSymbolTable(node);
        StructInfo structInfo = (StructInfo) symbolTable.lookup(node.getIdentifier());
        Integer index = structInfo.getIndex();

        mn.instructions.add(new LdcInsnNode(node.getLiteral()));
        mn.instructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, node.getStructIdentifier()));

        mn.instructions.add(new VarInsnNode(type.getOpcode(Opcodes.ISTORE), index));
    }

    @Override
    public void visit(StructDefinitionStatement node) throws ASTVisitorException {

        ClassNode structNode = new ClassNode();
        structNode.access = Opcodes.ACC_PUBLIC;
        structNode.version = Opcodes.V1_5;
        structNode.name = node.getIdentifier();

        structNode.superName = "java/lang/Object";

        List<Info> arrays = new ArrayList<>();

        SimpleSymbolTable<Info> symbolTable = ASTUtils.getSafeSymbolTable(node);
        Info struct = symbolTable.lookup(node.getIdentifier());

        //add fields
        for(VariableDefinitionStatement vs : node.getVariableDefinitions()){
            Info structVar = ((StructInfo) struct).containsVariable(vs.getId());
            Type varType = (Type)structVar.getValue();
            if(varType.toString().startsWith("[")){
                arrays.add(structVar);
            }
            FieldNode fn = new FieldNode(Opcodes.ACC_PUBLIC, vs.getId(), varType.toString(), null, null);
            structNode.fields.add(fn);


        }

        // create constructor
        MethodNode smn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        smn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        smn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));

        //initialize arrays

        for(Info a : arrays){

            int size = a.getArraySize();


            Type type = (Type) a.getValue();
            smn.instructions.add(new LdcInsnNode(size));
            if (a instanceof StructInfo) {
                smn.instructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, a.getStructId()));
            } else {
                smn.instructions.add(new IntInsnNode(Opcodes.NEWARRAY, TypeUtils.getTypeCode((type.getElementType()))));
            }

            smn.instructions.add(new VarInsnNode(Opcodes.ASTORE, a.getIndex()));

            smn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            smn.instructions.add(new VarInsnNode(Opcodes.ALOAD, a.getIndex()));
            smn.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, structNode.name, a.getId(), type.toString()));

        }

        smn.instructions.add(new InsnNode(Opcodes.RETURN));
        smn.maxLocals = 1;
        smn.maxStack = 1;
        structNode.methods.add(smn);



        //write class to a file

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        structNode.accept(cv);
        byte[] code = cw.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(node.getIdentifier()+".class");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            fos.write(code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        cl.register(node.getIdentifier(), code);

    }

    private void backPatch(List<JumpInsnNode> list, LabelNode labelNode) {
        if (list == null) {
            return;
        }
        for (JumpInsnNode instr : list) {
            instr.label = labelNode;
        }
    }

    /**
     * Cast the top of the stack to a particular type
     */
    private void widen(Type target, Type source) {
        if (source.equals(target)) {
            return;
        }

        if (source.equals(Type.BOOLEAN_TYPE)) {
            if (target.equals(Type.INT_TYPE) ||target.equals(Type.FLOAT_TYPE) || target.equals(Type.CHAR_TYPE) ) {
                // nothing
            } else if (target.equals(TypeUtils.STRING_TYPE)) {
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "toString",
                        "(Z)Ljava/lang/String;"));
            }
        } else if (source.equals(Type.INT_TYPE)) {
            if (target.equals(TypeUtils.STRING_TYPE)) {
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "toString",
                        "(I)Ljava/lang/String;"));
            } else if(target.equals(Type.FLOAT_TYPE))
                mn.instructions.add(new InsnNode(Opcodes.I2F));
        } else if (source.equals(Type.FLOAT_TYPE)) {
            if (target.equals(TypeUtils.STRING_TYPE)) {
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "toString",
                        "(D)Ljava/lang/String;"));
            }
        }
    }

    private void handleBooleanOperator(Expression node, Operator op, Type type) throws ASTVisitorException {
        List<JumpInsnNode> trueList = new ArrayList<>();

        if (type.equals(Type.FLOAT_TYPE)) {
            mn.instructions.add(new InsnNode(Opcodes.FCMPG));
            JumpInsnNode jmp = null;
            switch (op){
                case EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IFEQ, null);
                    mn.instructions.add(jmp);
                    break;
                case NEQUAL:
                    jmp = new JumpInsnNode(Opcodes.IFNE, null);
                    mn.instructions.add(jmp);
                    break;
                case GT:
                    jmp = new JumpInsnNode(Opcodes.IFGT, null);
                    mn.instructions.add(jmp);
                    break;
                case GE:
                    jmp = new JumpInsnNode(Opcodes.IFGE, null);
                    mn.instructions.add(jmp);
                    break;
                case LT:
                    jmp = new JumpInsnNode(Opcodes.IFLT, null);
                    mn.instructions.add(jmp);
                    break;
                case LE:
                    jmp = new JumpInsnNode(Opcodes.IFLE, null);
                    mn.instructions.add(jmp);
                    break;
                default:
                    ASTUtils.error(node, "Operator not supported");
                    break;
            }

            trueList.add(jmp);

        } else if(type.equals(Type.BOOLEAN_TYPE)){

            JumpInsnNode jmp = null;
            switch (op){
                case AND:
                    jmp = new JumpInsnNode(Opcodes.LAND, null);
                    mn.instructions.add(jmp);
                    break;
                case OR:
                    jmp = new JumpInsnNode(Opcodes.LOR, null);
                    mn.instructions.add(jmp);
                    break;
            }
            trueList.add(jmp);
        } else {
            JumpInsnNode jmp = null;
            switch (op) {
                case EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPEQ, null);
                    mn.instructions.add(jmp);
                    break;
                case NEQUAL:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPNE, null);
                    mn.instructions.add(jmp);
                    break;
                case GT:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPGT, null);
                    mn.instructions.add(jmp);
                    break;
                case GE:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPGE, null);
                    mn.instructions.add(jmp);
                    break;
                case LT:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPLT, null);
                    mn.instructions.add(jmp);
                    break;
                case LE:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPLE, null);
                    mn.instructions.add(jmp);
                    break;
                default:
                    ASTUtils.error(node, "Operator not supported");
                    break;
            }
            trueList.add(jmp);
        }
        ASTUtils.setTrueList(node, trueList);
        List<JumpInsnNode> falseList = new ArrayList<JumpInsnNode>();
        JumpInsnNode jmp = new JumpInsnNode(Opcodes.GOTO, null);
        mn.instructions.add(jmp);
        falseList.add(jmp);
        ASTUtils.setFalseList(node, falseList);
    }

    /**
     * Assumes top of stack contains two strings
     */
    private void handleStringOperator(ASTNode node, Operator op) throws ASTVisitorException {
        if (op.equals(Operator.PLUS)) {
            mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"));
            mn.instructions.add(new InsnNode(Opcodes.DUP));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V"));
            mn.instructions.add(new InsnNode(Opcodes.SWAP));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
            mn.instructions.add(new InsnNode(Opcodes.SWAP));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                    "()Ljava/lang/String;"));
        } else if (op.isRelational()) {
            LabelNode trueLabelNode = new LabelNode();
            switch (op) {
                case EQUAL:
                    mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals",
                            "(Ljava/lang/Object;)Z"));
                    mn.instructions.add(new JumpInsnNode(Opcodes.IFNE, trueLabelNode));
                    break;
                case NEQUAL:
                    mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals",
                            "(Ljava/lang/Object;)Z"));
                    mn.instructions.add(new JumpInsnNode(Opcodes.IFEQ, trueLabelNode));
                    break;
                default:
                    ASTUtils.error(node, "Operator not supported on strings");
                    break;
            }
            mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
            LabelNode endLabelNode = new LabelNode();
            mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabelNode));
            mn.instructions.add(trueLabelNode);
            mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
            mn.instructions.add(endLabelNode);
        } else {
            ASTUtils.error(node, "Operator not recognized");
        }
    }

    private void handleLogicalOperations(BinaryExpression node, Operator operator) throws ASTVisitorException {
        List<JumpInsnNode> trueList = new ArrayList<>();
        List<JumpInsnNode> falseList = new ArrayList<>();
        JumpInsnNode jmp = null;
        node.getExpression1().accept(this);
        switch (operator){
            case AND:
                jmp = new JumpInsnNode(Opcodes.IFEQ, null); //ifeq -> if value is 0
                mn.instructions.add(jmp);
                falseList.add(jmp);
                node.getExpression2().accept(this);
                jmp = new JumpInsnNode(Opcodes.IFEQ, null);
                mn.instructions.add(jmp);
                falseList.add(jmp);
                jmp = new JumpInsnNode(Opcodes.GOTO, null);
                mn.instructions.add(jmp);
                trueList.add(jmp);
                break;
            case OR:
                jmp = new JumpInsnNode(Opcodes.IFNE, null);
                mn.instructions.add(jmp);
                trueList.add(jmp);
                node.getExpression2().accept(this);
                jmp = new JumpInsnNode(Opcodes.IFNE, null);
                mn.instructions.add(jmp);
                trueList.add(jmp);
                jmp = new JumpInsnNode(Opcodes.GOTO, null);
                mn.instructions.add(jmp);
                falseList.add(jmp);
                break;

            default:
                ASTUtils.error(node, "operator not supported");
                break;
        }

        ASTUtils.setTrueList(node, trueList);
        ASTUtils.setFalseList(node, falseList);

    }

    //push bool result in stack
    private void calculateBooleanBinary(BinaryExpression node, Operator op, Type type) throws ASTVisitorException {
        node.getExpression1().accept(this);
        LabelNode trueLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();
        switch (op){
            case AND:
                mn.instructions.add(new JumpInsnNode(Opcodes.IFEQ, trueLabel));
                node.getExpression2().accept(this);
                mn.instructions.add(new JumpInsnNode(Opcodes.IFEQ, trueLabel));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_1));

                mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabel));
                mn.instructions.add(trueLabel);
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                mn.instructions.add(endLabel);
                break;
            case OR:
                mn.instructions.add(new JumpInsnNode(Opcodes.IFNE, trueLabel));
                node.getExpression2().accept(this);
                mn.instructions.add(new JumpInsnNode(Opcodes.IFNE, trueLabel));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabel));
                mn.instructions.add(trueLabel);
                mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
                mn.instructions.add(endLabel);

                break;
            default:

                break;
        }
    }
    private void handleNonBooleanBinary(ASTNode node, Operator op, Type type) throws ASTVisitorException {

        if (op.equals(Operator.PLUS)) {
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.IADD)));
        } else if (op.equals(Operator.MINUS)) {
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.ISUB)));

        } else if (op.equals(Operator.MULTIPLY)) {
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.IMUL)));

        } else if (op.equals(Operator.DIVISION)) {
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.IDIV)));

        } else if (op.isRelational()) {
            if (type.equals(Type.FLOAT_TYPE)) {
                mn.instructions.add(new InsnNode(Opcodes.FCMPG));
                JumpInsnNode jmp = null;
                switch (op) {
                    case EQUAL:
                        jmp = new JumpInsnNode(Opcodes.IFEQ, null);
                        mn.instructions.add(jmp);
                        break;
                    case NEQUAL:
                        jmp = new JumpInsnNode(Opcodes.IFNE, null);
                        mn.instructions.add(jmp);
                        break;
                    case GT:
                        jmp = new JumpInsnNode(Opcodes.IFGT, null);
                        mn.instructions.add(jmp);
                        break;
                    case GE:
                        jmp = new JumpInsnNode(Opcodes.IFGE, null);
                        mn.instructions.add(jmp);
                        break;
                    case LT:
                        jmp = new JumpInsnNode(Opcodes.IFLT, null);
                        mn.instructions.add(jmp);
                        break;
                    case LE:
                        jmp = new JumpInsnNode(Opcodes.IFLE, null);
                        mn.instructions.add(jmp);
                        break;
                    default:
                        ASTUtils.error(node, "Operator not supported");
                        break;
                }
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                LabelNode endLabelNode = new LabelNode();
                mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabelNode));
                LabelNode trueLabelNode = new LabelNode();
                jmp.label = trueLabelNode;
                mn.instructions.add(trueLabelNode);
                mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
                mn.instructions.add(endLabelNode);
            } else if (type.equals(Type.INT_TYPE)) {
                LabelNode trueLabelNode = new LabelNode();
                switch (op) {
                    case EQUAL:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, trueLabelNode));
                        break;
                    case NEQUAL:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPNE, trueLabelNode));
                        break;
                    case GT:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGT, trueLabelNode));
                        break;
                    case GE:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, trueLabelNode));
                        break;
                    case LT:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPLT, trueLabelNode));
                        break;
                    case LE:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPLE, trueLabelNode));
                        break;
                    default:
                        break;
                }
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                LabelNode endLabelNode = new LabelNode();
                mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabelNode));
                mn.instructions.add(trueLabelNode);
                mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
                mn.instructions.add(endLabelNode);
            } else {
                ASTUtils.error(node, "Cannot compare such types.");
            }
        } else {
            ASTUtils.error(node, "Operator not recognized.");
        }
    }


}
