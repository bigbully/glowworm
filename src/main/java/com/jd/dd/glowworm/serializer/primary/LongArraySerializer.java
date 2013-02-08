
package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class LongArraySerializer implements ObjectSerializer {

    public static LongArraySerializer instance = new LongArraySerializer();

    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ARRAY_LONG);
        }
        long[] array = (long[]) object;

        serializer.writeLongArray(array);
    }
}
