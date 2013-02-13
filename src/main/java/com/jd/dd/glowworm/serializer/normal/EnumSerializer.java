
package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;


public class EnumSerializer implements ObjectSerializer {

    public final static EnumSerializer instance = new EnumSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Enum<?> e = (Enum<?>) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ENUM);
            serializer.writeString(object.getClass().getName());
        }
        serializer.writeString(e.name());
    }
}
