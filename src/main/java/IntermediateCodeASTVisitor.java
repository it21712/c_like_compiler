/*
import ast.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.objectweb.asm.tree.JumpInsnNode;
import threeaddr.*;
import types.TypeWidth;

import java.util.*;

public class IntermediateCodeASTVisitor implements ASTVisitor {


    private final Program program;

    private final Deque<String> stack;

    private int temp;

    public IntermediateCodeASTVisitor(){
        program = new Program();
        stack = new ArrayDeque<>();
        temp = 0;
    }

    private String createTemp(){
        return "t"+ temp++;
    }

    public Program getProgram(){return program;}


    @Override
    public void visit(CompUnit node) throws ASTVisitorException {
        Statement s = null, ps;
        Iterator<Statement> it = node.getStatements().iterator();
        while (it.hasNext()) {
            ps = s;
            s = it.next();

            if (ps != null && !ASTUtils.getNextList(ps).isEmpty()) {
                Program.backpatch(ASTUtils.getNextList(ps), program.addNewLabel());
            }

            s.accept(this);

        }
        if (s != null && !ASTUtils.getNextList(s).isEmpty()) {
            Program.backpatch(ASTUtils.getNextList(s), program.addNewLabel());
        }
        //for(Statement s : node.getStatements()) s.accept(this);
    }

    @Override
    public void visit(RootUnit node) throws ASTVisitorException {
        for(CompUnit c : node.getCompUnits())
            c.accept(this);
    }


    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {

        ArrayList<String> leftTokens = new ArrayList<>(2);

        //setfield instruction case
        if(node.getExpression0() instanceof ExprDotIdentifierExpression ||
                node.getExpression0() instanceof ExpressionDotArrayElementExpression){
            ASTUtils.setLeftSideExpressionProperty(node.getExpression0(), true);
            node.getExpression0().accept(this);
            String field = stack.pop();
            String structId = stack.pop();
            leftTokens.add(field);
            leftTokens.add(structId);

        }
        else {
            node.getExpression0().accept(this);
            leftTokens.add(stack.pop());
        }

        node.getExpression().accept(this);
        if(leftTokens.size() == 2) {   //left token is a struct field
            program.add(new SetFieldInstr(leftTokens.get(1), leftTokens.get(0), stack.pop()));
            return;
        }
        String rightT = stack.pop();
        program.add(new AssignInstr(rightT, leftTokens.get(0)));
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
            if(ps != null && !ASTUtils.getNextList(ps).isEmpty())
                Program.backpatch(ASTUtils.getNextList(ps), program.addNewLabel());

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

        node.getExpression1().accept(this);
        String t1 = stack.pop();
        node.getExpression2().accept(this);
        String t2 = stack.pop();


        if(ASTUtils.isBooleanExpression(node)){
            */
/*if(!node.getOperator().isRelational() || !node.getOperator().isLogical()) {

                ASTUtils.error(node, "Non boolean expression used as a boolean");
            }*//*


            CondJumpInstr condJumpInstr = new CondJumpInstr(node.getOperator(), t1, t2, null);
            GotoInstr gotoInstr = new GotoInstr();

            program.add(condJumpInstr);
            program.add(gotoInstr);

            ASTUtils.getTrueList(node).add(condJumpInstr);
            ASTUtils.getFalseList(node).add(gotoInstr);

        }
        else{

            String t = createTemp();
            stack.push(t);

            program.add(new BinaryOpInstr(node.getOperator(), t1, t2, t));


        }

    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);

        String t1 = stack.pop();
        String t = createTemp();
        stack.push(t);
        program.add(new UnaryOpInstr(node.getOperator(), t1, t));
    }

    @Override
    public void visit(IdentifierExpression node) throws ASTVisitorException {
        stack.push(node.getIdentifier());
    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        String t = createTemp();
        program.add(new AssignInstr(node.getLiteral().toString(), t));
        stack.push(t);
    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        String t = createTemp();
        program.add(new AssignInstr(node.getLiteral().toString(), t));
        stack.push(t);
    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        String t = createTemp();
        stack.push(t);
        program.add(new AssignInstr("\"" + StringEscapeUtils.escapeJava(node.getLiteral()) + "\"", t));
    }

    @Override
    public void visit(CharLiteralExpression node) throws ASTVisitorException {
        String t = createTemp();
        stack.push(t);
        program.add(new AssignInstr("\"" + StringEscapeUtils.escapeJava(String.valueOf(node.getLiteral())) + "\"", t));
    }

    @Override
    public void visit(BooleanLiteralExpression node) throws ASTVisitorException {

        if(ASTUtils.isBooleanExpression(node)){
            CondJumpInstr condJumpInstr = new CondJumpInstr(null, Boolean.toString(node.getLiteral()), "", null);
            GotoInstr gotoInstr = new GotoInstr();

            program.add(condJumpInstr);
            program.add(gotoInstr);

            ASTUtils.getTrueList(node).add(condJumpInstr);
            ASTUtils.getFalseList(node).add(gotoInstr);
            return;
        }

        String t = createTemp();
        stack.push(t);
        program.add(new AssignInstr(String.valueOf(node.getLiteral()), t));
    }

    @Override
    public void visit(CharacterLiteralExpression node) throws ASTVisitorException {
        String t = createTemp();
        stack.push(t);
        program.add(new AssignInstr("\"" + StringEscapeUtils.escapeJava(String.valueOf(node.getLiteral())) + "\"", t));
    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
        String t1 = stack.pop();
        String t = createTemp();
        stack.push(t);
        program.add(new AssignInstr(t1, t));
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        ASTUtils.setBooleanExpression(node.getExpression(), true);

        LabelInstr beginLbl = program.addNewLabel();
        node.getExpression().accept(this);
        LabelInstr beginStmtLbl = program.addNewLabel();
        Program.backpatch(ASTUtils.getTrueList(node.getExpression()), beginStmtLbl);

        node.getStatement().accept(this);
        program.add(new GotoInstr(beginLbl));
        Program.backpatch(ASTUtils.getNextList(node.getStatement()), beginLbl);
        Program.backpatch(ASTUtils.getContinueList(node.getStatement()), beginLbl);

        ASTUtils.getNextList(node).addAll(ASTUtils.getFalseList(node.getExpression()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getBreakList(node.getStatement()));


    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {

        ASTUtils.setBooleanExpression(node.getExpression(), true);

        node.getExpression().accept(this);

        LabelInstr trueLbl = program.addNewLabel();

        node.getStatement().accept(this);

        Program.backpatch(ASTUtils.getTrueList(node.getExpression()), trueLbl);

        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getStatement()));
        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getStatement()));

        ASTUtils.getNextList(node).addAll(ASTUtils.getFalseList(node.getExpression()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getStatement()));
    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        ASTUtils.setBooleanExpression(node.getExpression(), true);

        node.getExpression().accept(this);

        LabelInstr trueLbl = program.addNewLabel();
        node.getIfStatement().accept(this);
        GotoInstr nextGoto = new GotoInstr();
        program.add(nextGoto);

        LabelInstr falseLbl = program.addNewLabel();
        node.getElseStatement().accept(this);

        Program.backpatch(ASTUtils.getTrueList(node.getExpression()), trueLbl);
        Program.backpatch(ASTUtils.getFalseList(node.getExpression()), falseLbl);

        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getIfStatement()));
        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getElseStatement()));

        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getIfStatement()));
        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getElseStatement()));

        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getIfStatement()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getElseStatement()));

        ASTUtils.getNextList(node).add(nextGoto);

    }
//TODO goto instruction to exit function
    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        String t1 = stack.pop();
        ReturnInstr instr = new ReturnInstr(t1);
        program.add(instr);
    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {
        GotoInstr gotoInstr = new GotoInstr();
        program.add(gotoInstr);
        ASTUtils.getBreakList(node).add(gotoInstr);
    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {
        GotoInstr gotoInstr = new GotoInstr();
        program.add(gotoInstr);
        ASTUtils.getContinueList(node).add(gotoInstr);
    }

    @Override
    public void visit(ExpressionSemicolonStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(ParameterDeclarationStatement node) throws ASTVisitorException {

    }

    @Override
    public void visit(FunctionDefinitionStatement node) throws ASTVisitorException {

        program.add(new FunctionDefLabelInstr(node.getIdentifier()));

        Statement s = null, ps;

        Iterator<Statement> it = node.getStatements().iterator();

        while (it.hasNext()){
            ps = s;
            s = it.next();

            if(ps != null && !ASTUtils.getNextList(ps).isEmpty())
                Program.backpatch(ASTUtils.getNextList(ps), program.addNewLabel());

            s.accept(this);
        }

        if(s != null && !ASTUtils.getNextList(s).isEmpty())
            Program.backpatch(ASTUtils.getNextList(s), program.addNewLabel());
    }

    @Override
    public void visit(IdentifierParenthesisExpression node) throws ASTVisitorException {

        int paramCount = node.getExpressions().size();

        for(Expression e : node.getExpressions()){
            e.accept(this);
            String t1 = stack.pop();
            ParamInstr instr = new ParamInstr(t1);
            program.add(instr);
        }
        CallInstr callInstr = new CallInstr(node.getIdentifier(), paramCount);
        //System.out.println("PRINT TYPE: "+ASTUtils.getSafeType(node) + "\t" + node.getLine()+":"+node.getColumn());
        if(ASTUtils.getSafeType(node).toString().contains("V")){
            program.add(callInstr);
            return;
        }
        String t = createTemp();
        AssignInstr assignInstr = new AssignInstr(callInstr.emit(), t);
        program.add(assignInstr);
        stack.push(t);

    }

    @Override
    public void visit(IdentifierBracketExpression node) throws ASTVisitorException {
        //array
        node.getExpression().accept(this);
        String t1 = stack.pop();
        int width = TypeWidth.getTypeWidth(ASTUtils.getSafeType(node));
        String t2 = createTemp();
        program.add(new BinaryOpInstr(Operator.MULTIPLY, Integer.toString(width), t1, t2));
        //String t3 = createTemp();
        String array = node.getIdentifier()+"["+ t2 + "]";

        //program.add(new AssignInstr(array, t3));
        //stack.push(t3);
        stack.push(array);

    }

    @Override
    public void visit(ExprDotIdentifierExpression node) throws ASTVisitorException {


        node.getExpression().accept(this); //push struct identifier to the stack
        String field = node.getIdentifier();
        stack.push(field);
        if(!ASTUtils.getLeftSideExpressionProperty(node)) {
            String t = createTemp();
            String _field = stack.pop();
            String _structId = stack.pop();
            GetFieldInstr getFieldInstr = new GetFieldInstr(_structId, _field);
            program.add(new AssignInstr(getFieldInstr.emit(), t));

            stack.push(t);

        }
    }

    @Override
    public void visit(ExpressionDotArrayElementExpression node) throws ASTVisitorException {

        */
/*node.getExpression().accept(this);
        String structId = stack.pop();
        node.getIdentifierBracketExpression().accept(this);
        String arrayT = stack.pop();

        String t = createTemp();
        IdentifierExpression ie = (IdentifierExpression)node.getExpression();
        GetFieldInstr getFieldInstr = new GetFieldInstr(ie.getIdentifier(), arrayT);

        program.add(new AssignInstr(getFieldInstr.emit(), t));
        stack.push(t);*//*

        node.getExpression().accept(this); //push struct id to stack;
        node.getIdentifierBracketExpression().accept(this); //push array element to stack;

        if(!ASTUtils.getLeftSideExpressionProperty(node)){
            String t = createTemp();

            String field  = stack.pop();
            String structId = stack.pop();

            GetFieldInstr getFieldInstr = new GetFieldInstr(structId, field);
            program.add(new AssignInstr(getFieldInstr.emit(), t));

            stack.push(t);
        }

    }

    @Override
    public void visit(VariableDefinition node) throws ASTVisitorException {
    }

    @Override
    public void visit(VariableArrayDefinition node) throws ASTVisitorException {
    }

    @Override
    public void visit(StructDeclarationStatement node) throws ASTVisitorException {
    }

    @Override
    public void visit(StructArrayDeclaration node) throws ASTVisitorException {
    }

    @Override
    public void visit(StructDefinitionStatement node) throws ASTVisitorException {
    }
}
*/
