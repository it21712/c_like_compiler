package ast;

public enum TypeSpecifier {

    CHAR("char"),
    BOOL("bool"),
    INT("int"),
    FLOAT("float"),
    VOID("void"),
    STRUCT("struct");

    private String stringValue;

    TypeSpecifier(String type) {
        this.stringValue = type;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
