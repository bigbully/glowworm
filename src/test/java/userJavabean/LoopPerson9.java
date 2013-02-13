package userJavabean;

import java.util.Set;

public class LoopPerson9 {

    private Integer integer;
    private LoopPerson9 lp;
    private Set<LoopPerson9> set;

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public LoopPerson9 getLp() {
        return lp;
    }

    public void setLp(LoopPerson9 lp) {
        this.lp = lp;
    }

    public Set<LoopPerson9> getSet() {
        return set;
    }

    public void setSet(Set<LoopPerson9> set) {
        this.set = set;
    }
}
