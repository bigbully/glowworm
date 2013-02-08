package javabean;

import java.util.Set;

public class LoopPerson4 {

    private byte b;
    private Set<LoopPerson4> set;
    //    private HashMap<LoopPerson4, Object> map1;
//    private Map<Integer, LoopPerson4> map2;
    private LoopPerson4 brother;

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public Set<LoopPerson4> getSet() {
        return set;
    }

    public void setSet(Set<LoopPerson4> set) {
        this.set = set;
    }

//    public HashMap<LoopPerson4, Object> getMap1() {
//        return map1;
//    }
//
//    public void setMap1(HashMap<LoopPerson4, > map1) {
//        this.map1 = map1;
//    }

//    public Map<Integer, LoopPerson4> getMap2() {
//        return map2;
//    }
//
//    public void setMap2(Map<Integer, LoopPerson4> map2) {
//        this.map2 = map2;
//    }

    public LoopPerson4 getBrother() {
        return brother;
    }

    public void setBrother(LoopPerson4 brother) {
        this.brother = brother;
    }
}
