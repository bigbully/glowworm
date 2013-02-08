package javabean;

import java.util.HashMap;

public class LoopPerson6 {

    private Integer integer;
    private HashMap<LoopPerson6, String> map;

    public HashMap<LoopPerson6, String> getMap() {
        return map;
    }

    public void setMap(HashMap<LoopPerson6, String> map) {
        this.map = map;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }
}
