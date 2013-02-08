package javabean;

import java.util.List;

public class LoopPerson8 {

    private Integer integer;
    private LoopPerson8 lp;
    private List<LoopPerson8> list;

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public List<LoopPerson8> getList() {
        return list;
    }

    public void setList(List<LoopPerson8> list) {
        this.list = list;
    }

    public LoopPerson8 getLp() {
        return lp;
    }

    public void setLp(LoopPerson8 lp) {
        this.lp = lp;
    }
}
