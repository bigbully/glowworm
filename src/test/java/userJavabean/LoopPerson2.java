package userJavabean;

public class LoopPerson2 {

    private byte b;
    private LoopPerson2[] loopPerson2s;
    private LoopPerson2 brother;

    public LoopPerson2[] getLoopPerson2s() {
        return loopPerson2s;
    }

    public void setLoopPerson2s(LoopPerson2[] loopPerson2s) {
        this.loopPerson2s = loopPerson2s;
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public LoopPerson2 getBrother() {
        return brother;
    }

    public void setBrother(LoopPerson2 brother) {
        this.brother = brother;
    }
}
