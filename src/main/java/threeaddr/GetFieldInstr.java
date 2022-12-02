package threeaddr;

public class GetFieldInstr implements Instruction {

    private String identifier;
    private String field;


    public GetFieldInstr(String identifier, String field) {
        this.identifier = identifier;
        this.field = field;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String emit() {
        return "getfield " + identifier + ", " + field;
    }
}
