/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package ast;

/**
 * Abstract syntax tree visitor.
 */
public interface ASTVisitor {

    void visit(CompUnit node) throws ASTVisitorException;

    void visit(RootUnit node) throws ASTVisitorException;

    void visit(AssignmentStatement node) throws ASTVisitorException;

    void visit(PrintStatement node) throws ASTVisitorException;

    void visit(CompoundStatement node) throws ASTVisitorException;

    void visit(BinaryExpression node) throws ASTVisitorException;

    void visit(UnaryExpression node) throws ASTVisitorException;

    void visit(IdentifierExpression node) throws ASTVisitorException;

    void visit(FloatLiteralExpression node) throws ASTVisitorException;

    void visit(IntegerLiteralExpression node) throws ASTVisitorException;

    void visit(StringLiteralExpression node) throws ASTVisitorException;

    void visit(CharLiteralExpression node) throws ASTVisitorException;

    void visit(BooleanLiteralExpression node) throws ASTVisitorException;

    void visit(CharacterLiteralExpression node) throws ASTVisitorException;

    void visit(ParenthesisExpression node) throws ASTVisitorException;

    void visit(WhileStatement node) throws ASTVisitorException;

    void visit(IfStatement node) throws ASTVisitorException;

    void visit(IfElseStatement node) throws ASTVisitorException;

    void visit(ReturnStatement node) throws ASTVisitorException;

    void visit(BreakStatement node) throws ASTVisitorException;

    void visit(ContinueStatement node) throws ASTVisitorException;

    void visit(ExpressionSemicolonStatement node) throws ASTVisitorException;

    void visit(ParameterDeclarationStatement node) throws ASTVisitorException;

    void visit(FunctionDefinitionStatement node) throws ASTVisitorException;

    void visit(IdentifierParenthesisExpression node) throws ASTVisitorException;

    void visit(IdentifierBracketExpression node) throws ASTVisitorException;

    void visit(ExprDotIdentifierExpression node) throws ASTVisitorException;

    void visit(VariableDefinition node) throws ASTVisitorException;

    void visit(VariableArrayDefinition node) throws ASTVisitorException;

    void visit(StructDeclarationStatement node) throws ASTVisitorException;

    void visit(StructArrayDeclaration node) throws ASTVisitorException;

    void visit(StructDefinitionStatement node) throws ASTVisitorException;

    void visit(ExpressionDotArrayElementExpression expressionDotArrayElementExpression) throws ASTVisitorException;
}
