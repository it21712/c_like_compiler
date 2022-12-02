package threeaddr;

public class ReturnInstr implements Instruction {

    private String arg;

    public ReturnInstr(String arg){this.arg = arg;}

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    @Override
    public String emit() {
        return "return " + arg;
    }
}
