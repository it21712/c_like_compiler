package threeaddr;

public class ArrayItemAddressInstr implements Instruction {

    int index;
    int width;

    public ArrayItemAddressInstr(int index, int width) {
        this.index = index;
        this.width = width;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String emit() {
        return null;
    }
}
