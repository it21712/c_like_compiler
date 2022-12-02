package ast;

import java.util.Arrays;

public enum  ReturnType {

    BOOL("bool"),
    FLOAT("float"),
    INT("int"),
    CHAR("char"),
    VOID("void");

    private String type;

    ReturnType(String type){this.type = type;}
    public String getType(){return type;}

    @Override
    public String toString() {
        return type;
    }

}
