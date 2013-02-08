
package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerSerializer implements ObjectSerializer {

    public final static AtomicIntegerSerializer instance = new AtomicIntegerSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        AtomicInteger value = (AtomicInteger) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ATOMIC_INT);
        }
        serializer.writeInt(value.get());
    }
}
