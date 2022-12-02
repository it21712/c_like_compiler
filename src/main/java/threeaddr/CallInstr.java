package threeaddr;

public class CallInstr implements Instruction{

    private String functionName;
    private int argC;


    public CallInstr(String functionName, int argC) {
        this.functionName = functionName;
        this.argC = argC;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getArgC() {
        return argC;
    }

    public void setArgC(int argC) {
        this.argC = argC;
    }

    @Override
    public String emit() {
        return "call " + functionName + ", " + argC;
    }
}
