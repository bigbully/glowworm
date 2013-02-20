package userJavabean;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class Glowworm_Serializer_1 implements ObjectSerializer {

    public ObjectSerializer abcSerializer;

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (abcSerializer == null) {
            abcSerializer = serializer.getObjectWriter(Person1.class);
        }
    }
}
