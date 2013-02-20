package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class IntegerSerializer implements ObjectSerializer {

    public static IntegerSerializer instance = new IntegerSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Number value = (Number) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.INT);
        }
        serializer.writeInt(value.intValue());
    }
}
