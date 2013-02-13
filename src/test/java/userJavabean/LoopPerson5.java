package userJavabean;

import java.util.HashMap;

public class LoopPerson5 {

    private Integer integer;
    private HashMap<String, LoopPerson5> map;

    public HashMap<String, LoopPerson5> getMap() {
        return map;
    }

    public void setMap(HashMap<String, LoopPerson5> map) {
        this.map = map;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }
}
