package userJavabean;

import java.util.Map;

public class Person11 {

    private Integer integer;
    private Person11 p;
    private Map map1;
    private Map<Object, Object> map2;

    public Person11 getP() {
        return p;
    }

    public void setP(Person11 p) {
        this.p = p;
    }

    public Map getMap1() {
        return map1;
    }

    public void setMap1(Map map1) {
        this.map1 = map1;
    }

    public Map<Object, Object> getMap2() {
        return map2;
    }

    public void setMap2(Map<Object, Object> map2) {
        this.map2 = map2;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }
}
