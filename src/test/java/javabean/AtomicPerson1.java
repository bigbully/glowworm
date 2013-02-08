package javabean;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicPerson1 {

    private AtomicInteger ai;
    private AtomicBoolean ab;
    private AtomicLong al;

    public AtomicInteger getAi() {
        return ai;
    }

    public void setAi(AtomicInteger ai) {
        this.ai = ai;
    }

    public AtomicBoolean getAb() {
        return ab;
    }

    public void setAb(AtomicBoolean ab) {
        this.ab = ab;
    }

    public AtomicLong getAl() {
        return al;
    }

    public void setAl(AtomicLong al) {
        this.al = al;
    }
}
