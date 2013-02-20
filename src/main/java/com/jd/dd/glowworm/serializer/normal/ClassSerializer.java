package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class ClassSerializer implements ObjectSerializer {

    public final static ClassSerializer instance = new ClassSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.CLASS);
        }
        Class clazz = (Class) object;
        serializer.writeString(clazz.getName());
    }
}
