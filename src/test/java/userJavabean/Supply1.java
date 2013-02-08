package userJavabean;

import java.util.concurrent.ConcurrentHashMap;

public class Supply1 {

    private ConcurrentHashMap theConHMap;

    public ConcurrentHashMap getTheConHMap() {
        return theConHMap;
    }

    public void setTheConHMap(ConcurrentHashMap theConHMap) {
        this.theConHMap = theConHMap;
    }
}
