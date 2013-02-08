package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class BooleanSerializer implements ObjectSerializer {

    public final static BooleanSerializer instance = new BooleanSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Boolean value = (Boolean) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.BOOLEAN);
        }
        serializer.writeBool(value);
    }
}
