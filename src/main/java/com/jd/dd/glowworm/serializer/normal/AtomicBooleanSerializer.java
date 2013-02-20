package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanSerializer implements ObjectSerializer {

    public final static AtomicBooleanSerializer instance = new AtomicBooleanSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        AtomicBoolean value = (AtomicBoolean) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ATOMIC_BOOL);
        }
        serializer.writeBool(value.get());
    }
}
