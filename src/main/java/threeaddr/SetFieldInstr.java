package threeaddr;

public class SetFieldInstr implements Instruction {

    private String identifier;
    private String field;
    private String value;

    public SetFieldInstr(String identifier, String field, String value) {
        this.identifier = identifier;
        this.field = field;
        this.value = value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String emit() {
        return "setfield " + identifier + ", " + field + ", " + value;
    }
}
