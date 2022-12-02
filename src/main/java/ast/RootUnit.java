package ast;

import java.util.ArrayList;
import java.util.List;

public class RootUnit extends ASTNode {

    private List<CompUnit> compUnits = new ArrayList<>();

    public RootUnit(){}

    public RootUnit(List<CompUnit> compUnits){
        this.compUnits = compUnits;
    }

    public List<CompUnit> getCompUnits() {
        return compUnits;
    }

    public void setCompUnits(List<CompUnit> compUnits) {
        this.compUnits = compUnits;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
}
