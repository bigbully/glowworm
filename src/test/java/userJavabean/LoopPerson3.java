package userJavabean;

import java.util.List;

public class LoopPerson3 {

    private byte b;
    private List<LoopPerson3> list;
    private LoopPerson3 brother;


    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public List<LoopPerson3> getList() {
        return list;
    }

    public void setList(List<LoopPerson3> list) {
        this.list = list;
    }

    public LoopPerson3 getBrother() {
        return brother;
    }

    public void setBrother(LoopPerson3 brother) {
        this.brother = brother;
    }
}
