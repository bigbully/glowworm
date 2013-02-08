package userJavabean;

import java.util.HashMap;
import java.util.Map;

public class User13 {

    private Map map;
    private HashMap hashMap;
    private Map<Integer, User1> genericMap;

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public HashMap getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap hashMap) {
        this.hashMap = hashMap;
    }

    public Map<Integer, User1> getGenericMap() {
        return genericMap;
    }

    public void setGenericMap(Map<Integer, User1> genericMap) {
        this.genericMap = genericMap;
    }

}
