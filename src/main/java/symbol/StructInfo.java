package symbol;

import org.objectweb.asm.Type;

import java.util.ArrayList;

/**An info class for structs, that
 *  also holds an array of type info of the struct's variable definitions, so that every struct variable can hold the struct defined variables*/
public class StructInfo extends Info{

    private ArrayList<Info> variableDefinitions = new ArrayList<>();
    private String fieldOf = "";

    public StructInfo(String id, Object value, ArrayList<Info> variableDefinitions) {
        super(id, value);
        this.variableDefinitions = variableDefinitions;
    }
    public StructInfo(String id, Object value, ArrayList<Info> variableDefinitions, Integer index) {
        super(id, value, index);
        this.variableDefinitions = variableDefinitions;

    }
    public StructInfo(String id, String structId, Object value, ArrayList<Info> variableDefinitions, Integer index) {
        super(id, structId, value, index);
        this.variableDefinitions = variableDefinitions;

    }
    public StructInfo(String id, String structId, Object value, int arraySize, ArrayList<Info> variableDefinitions, Integer index) {
        super(id, structId, value, arraySize, index);
        this.variableDefinitions = variableDefinitions;

    }

    public StructInfo(String id, Object value, boolean isFunction, ArrayList<Info> variableDefinitions) {
        super(id, value, isFunction);
        this.variableDefinitions = variableDefinitions;
    }

    public StructInfo(String id, Object value, String structId, ArrayList<Info> variableDefinitions) {
        super(id, value, structId);
        this.variableDefinitions = variableDefinitions;
    }

    public StructInfo(String id, Object value, boolean isFunction, String structId, ArrayList<Info> variableDefinitions) {
        super(id, value, isFunction, structId);
        this.variableDefinitions = variableDefinitions;
    }

    public ArrayList<Info> getVariableDefinitions() {
        return variableDefinitions;
    }

    public void setFieldOf(String structName){
        this.fieldOf = structName;
    }

    public String getFieldOf(){return this.fieldOf;}

    public void setVariableDefinitions(ArrayList<Info> variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
    }

    public Info containsVariable(String identifier){
        for(Info i : variableDefinitions){
            if(i.getId().equals(identifier)) return i;
        }
        return null;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        return super.equals(obj);
    }
}
