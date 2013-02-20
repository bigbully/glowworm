package userJavabean;

import java.util.HashMap;
import java.util.Map;

public class User12 {

    public User12() {
        innerUser = new InnerUser();
    }

    public void putMap(Map map) {
        innerUser.setMap(map);
    }

    public Map findMap() {
        return innerUser.getMap();
    }

    public void putHashMap(HashMap hashMap) {
        innerUser.setHashMap(hashMap);
    }

    public HashMap findHashMap() {
        return innerUser.getHashMap();
    }

    public void putGenericMap(Map<Integer, User1> genericMap) {
        innerUser.setGenericMap(genericMap);
    }

    public Map<Integer, User1> findGenericMap() {
        return innerUser.getGenericMap();
    }

    private InnerUser innerUser;

    public InnerUser getInnerUser() {
        return innerUser;
    }

    public void setInnerUser(InnerUser innerUser) {
        this.innerUser = innerUser;
    }

    private class InnerUser {
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
}
