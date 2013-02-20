package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class FloatSerializer implements ObjectSerializer {

    public static FloatSerializer instance = new FloatSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        float floatValue = ((Float) object).floatValue();
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.FLOAT);
        }
        serializer.writeFloat(floatValue);
    }
}
