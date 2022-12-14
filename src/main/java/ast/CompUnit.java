/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package ast;

import java.util.ArrayList;
import java.util.List;

public class CompUnit extends ASTNode {

    private List<Statement> statements;

    public CompUnit() {
        statements = new ArrayList<Statement>();
    }

    public CompUnit(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }



    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }

}
