package userJavabean;

import com.jd.dd.glowworm.util.Transient;

public class TransientBean3 {

    private Integer index;

    public Integer getIndex() {
        return index;
    }

    @Transient
    public void setIndex(Integer index) {
        this.index = index;
    }
}
