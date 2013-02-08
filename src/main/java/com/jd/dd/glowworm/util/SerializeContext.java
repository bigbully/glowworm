package com.jd.dd.glowworm.util;

public class SerializeContext {

    private Object obj;
    private Integer index;

    public SerializeContext(){}

    public SerializeContext(Object obj, Integer index){
        this.obj = obj;
        this.index = index;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
