package userJavabean;

import java.util.concurrent.ConcurrentHashMap;

public class Supply2 {

    private ConcurrentHashMap theConHMap;
    private Inner inner;

    public ConcurrentHashMap getTheConHMap() {
        return theConHMap;
    }

    public void setTheConHMap(ConcurrentHashMap theConHMap) {
        this.theConHMap = theConHMap;
    }

    public Inner getInner() {
        return inner;
    }

    public void setInner(Inner inner) {
        this.inner = inner;
    }

    private class Inner {

    }
}
