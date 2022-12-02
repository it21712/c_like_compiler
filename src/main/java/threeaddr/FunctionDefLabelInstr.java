package threeaddr;

public class FunctionDefLabelInstr implements Instruction {

    String functionName;

    public FunctionDefLabelInstr(String functionName){this.functionName = functionName;}

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String emit() {
        return "-- "+ functionName + " --";
    }
}
