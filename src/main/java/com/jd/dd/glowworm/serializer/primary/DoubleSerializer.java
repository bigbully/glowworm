package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class DoubleSerializer implements ObjectSerializer {

    public final static DoubleSerializer instance = new DoubleSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        double doubleValue = ((Double) object).doubleValue();
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.DOUBLE);
        }
        serializer.writeDouble(doubleValue);
    }
}
