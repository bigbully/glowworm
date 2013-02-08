package userJavabean;

public class LoopPerson1 {

    private byte b;
    private LoopPerson1 brother;

    public LoopPerson1 getBrother() {
        return brother;
    }

    public void setBrother(LoopPerson1 brother) {
        this.brother = brother;
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }
}
