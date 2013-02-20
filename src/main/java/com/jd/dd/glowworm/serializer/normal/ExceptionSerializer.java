package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.asm.Type;
import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.serializer.reflect.JavaBeanSerializer;

import java.io.IOException;

public class ExceptionSerializer extends JavaBeanSerializer {

    public ExceptionSerializer(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(Type.EXCEPTION);
        }
        super.write(serializer, object, false, extraParams);
    }

}
