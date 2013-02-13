package javabean;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class Glowworm_Serializer_1 implements ObjectSerializer {

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if(object == null || serializer.needConsiderRef(null) && serializer.isReference(object)){
            System.out.println(123);
        }
    }
}
