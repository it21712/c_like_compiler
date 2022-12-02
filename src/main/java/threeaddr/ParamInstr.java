package threeaddr;

public class ParamInstr implements Instruction{

    String param;

    public ParamInstr(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String emit() {
        return "param " + param;
    }
}
