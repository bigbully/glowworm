package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class LongSerializer implements ObjectSerializer {

    public final static LongSerializer instance = new LongSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        long longValue = ((Long) object).longValue();
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.LONG);
        }
        serializer.writeLong(longValue);
    }
}
