package ast;

public abstract class VariableDefinitionStatement  extends Statement {

    /**Method overriden by subclasses in order to get the identifier**/
    public String getId(){
        return  "";
    }
}
