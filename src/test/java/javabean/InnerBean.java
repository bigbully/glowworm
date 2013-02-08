package javabean;

public class InnerBean {

    private int i;
    private InnerBean ib;
    private Inner inner;

    public InnerBean() {
        this.inner = new Inner();
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public InnerBean getIb() {
        return ib;
    }

    public void setIb(InnerBean ib) {
        this.ib = ib;
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
