package javabean;

import com.jd.dd.glowworm.util.Transient;

public class TransientBean1 {

    @Transient
    private Integer index;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
