
package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongSerializer implements ObjectSerializer {

    public final static AtomicLongSerializer instance = new AtomicLongSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        AtomicLong value = (AtomicLong) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ATOMIC_LONG);
        }
        serializer.writeLong(value.get());
    }
}
