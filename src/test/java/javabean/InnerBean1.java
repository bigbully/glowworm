package javabean;

import java.util.Set;

public class InnerBean1 {

    private int i;
    private InnerBean1 ib;
    private Inner inner;
    private Set<Inner> innerSet;

    public InnerBean1() {
        this.inner = new Inner();
        this.inner.setInnerIndex(1);
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public InnerBean1 getIb() {
        return ib;
    }

    public void setIb(InnerBean1 ib) {
        this.ib = ib;
    }

    public Inner getInner() {
        return inner;
    }

    public void setInner(Inner inner) {
        this.inner = inner;
    }

    public Set<Inner> getInnerSet() {
        return innerSet;
    }

    public void setInnerSet(Set<Inner> innerSet) {
        this.innerSet = innerSet;
    }

    private class Inner {

        private int innerIndex;

        public int getInnerIndex() {
            return innerIndex;
        }

        public void setInnerIndex(int innerIndex) {
            this.innerIndex = innerIndex;
        }
    }
}
