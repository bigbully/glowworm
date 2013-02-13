package userJavabean;

import java.util.Map;

public class LoopPerson10 {

    private Integer integer;
    private LoopPerson10 lp;
    private Map<String, LoopPerson10> map1;
    private Map<LoopPerson10, String> map2;

    public LoopPerson10 getLp() {
        return lp;
    }

    public void setLp(LoopPerson10 lp) {
        this.lp = lp;
    }

    public Map<String, LoopPerson10> getMap1() {
        return map1;
    }

    public void setMap1(Map<String, LoopPerson10> map1) {
        this.map1 = map1;
    }

    public Map<LoopPerson10, String> getMap2() {
        return map2;
    }

    public void setMap2(Map<LoopPerson10, String> map2) {
        this.map2 = map2;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }
}
