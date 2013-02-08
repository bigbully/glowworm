package javabean;

import com.jd.dd.glowworm.util.Transient;

public class TransientBean2 {

    private Integer index;

    @Transient
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
