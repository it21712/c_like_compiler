/**
 * This code is part of the lab exercises for the Compilers course at Harokopio

 * University of Athens, Dept. of Informatics and Telematics.
 */

import ast.*;
import symbol.Info;
import symbol.SimpleSymbolTable;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Build symbol tables for each node of the AST.
 */
public class SymTableBuilderASTVisitor implements ASTVisitor {

	private final Deque<SimpleSymbolTable<Info>> stack;

	public SymTableBuilderASTVisitor() {
		stack = new ArrayDeque<>();
	}


	@Override
	public void visit(RootUnit node) throws ASTVisitorException {
		startScope();
		ASTUtils.setSymbolTable(node, stack.element());
		for( CompUnit c : node.getCompUnits())
			c.accept(this);
		endScope();
	}

	@Override
	public void visit(CompUnit node) throws ASTVisitorException {

		//startScope();
		ASTUtils.setSymbolTable(node, stack.element());
		for(Statement s : node.getStatements())
			s.accept(this);
		//endScope();

	}


	@Override
	public void visit(AssignmentStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression0().accept(this);
		node.getExpression().accept(this);
	}

	@Override
	public void visit(PrintStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression().accept(this);
	}

	@Override
	public void visit(CompoundStatement node) throws ASTVisitorException {
		startScope();
		ASTUtils.setSymbolTable(node, stack.element());
		for(Statement s : node.getStatements())
			s.accept(this);
		endScope();
	}

	@Override
	public void visit(BinaryExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression1().accept(this);
		node.getExpression2().accept(this);
	}

	@Override
	public void visit(UnaryExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression().accept(this);
	}

	@Override
	public void visit(IdentifierExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(FloatLiteralExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(StringLiteralExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(CharLiteralExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(BooleanLiteralExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(CharacterLiteralExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(ParenthesisExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression().accept(this);
	}

	@Override
	public void visit(WhileStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression().accept(this);
		node.getStatement().accept(this);
	}

	@Override
	public void visit(IfStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression().accept(this);
		node.getStatement().accept(this);
	}

	@Override
	public void visit(IfElseStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression().accept(this);
		node.getIfStatement().accept(this);
		node.getElseStatement().accept(this);
	}

	@Override
	public void visit(ReturnStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		if(node.getExpression() != null) node.getExpression().accept(this);
	}

	@Override
	public void visit(BreakStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(ContinueStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(ExpressionSemicolonStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression().accept(this);
	}

	@Override
	public void visit(ParameterDeclarationStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(FunctionDefinitionStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		startScope();
		for( ParameterDeclarationStatement p : node.getParameters() )
			p.accept(this);
		for( Statement s : node.getStatements() )
			s.accept(this);
		endScope();
	}

	@Override
	public void visit(IdentifierParenthesisExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		for(Expression e : node.getExpressions())
			e.accept(this);
	}

	@Override
	public void visit(IdentifierBracketExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		node.getExpression().accept(this);

	}

	@Override
	public void visit(ExprDotIdentifierExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());

		node.getExpression().accept(this);

	}

	@Override
	public void visit(ExpressionDotArrayElementExpression node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());

		node.getExpression().accept(this);
		node.getIdentifierBracketExpression().accept(this);

	}

	@Override
	public void visit(VariableDefinition node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(VariableArrayDefinition node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(StructDeclarationStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(StructArrayDeclaration node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
	}

	@Override
	public void visit(StructDefinitionStatement node) throws ASTVisitorException {
		ASTUtils.setSymbolTable(node, stack.element());
		startScope();
		for(VariableDefinitionStatement vs : node.getVariableDefinitions())
			vs.accept(this);
		endScope();
	}

	private void startScope() {
		SimpleSymbolTable<Info> oldSymTable = stack.peek();
		SimpleSymbolTable<Info> symTable = new SimpleSymbolTable<>(oldSymTable);
		stack.push(symTable);
	}

	private void endScope() {
		stack.pop();
	}


}
