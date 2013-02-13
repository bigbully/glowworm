package userJavabean;

import java.util.*;

public class LoopPerson11 {

    private Integer integer;
    private LoopInner loopInner;

    public LoopPerson11() {
        this.integer = 1;
        this.loopInner = new LoopInner();
        this.loopInner.setInteger(2);
        List<LoopPerson11> list = new ArrayList<LoopPerson11>();
        list.add(this);
        Set<LoopPerson11> set = new HashSet<LoopPerson11>();
        set.add(this);
        Map<String, LoopPerson11> map = new HashMap<String, LoopPerson11>();
        map.put("1", this);
        this.loopInner.setList(list);
        this.loopInner.setSet(set);
        this.loopInner.setMap(map);
    }

    public List<LoopPerson11> findInnerList() {
        return this.loopInner.getList();
    }

    public Set<LoopPerson11> findInnerSet() {
        return this.loopInner.getSet();
    }

    public Map<String, LoopPerson11> findInnerMap() {
        return this.getLoopInner().getMap();
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public LoopInner getLoopInner() {
        return loopInner;
    }

    public void setLoopInner(LoopInner loopInner) {
        this.loopInner = loopInner;
    }

    private class LoopInner {

        private Integer integer;
        private List<LoopPerson11> list;
        private Set set;
        private Map<String, LoopPerson11> map;

        private LoopInner() {

        }

        public Integer getInteger() {
            return integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        public List<LoopPerson11> getList() {
            return list;
        }

        public void setList(List<LoopPerson11> list) {
            this.list = list;
        }

        public Set getSet() {
            return set;
        }

        public void setSet(Set set) {
            this.set = set;
        }

        public Map<String, LoopPerson11> getMap() {
            return map;
        }

        public void setMap(Map<String, LoopPerson11> map) {
            this.map = map;
        }
    }
}
