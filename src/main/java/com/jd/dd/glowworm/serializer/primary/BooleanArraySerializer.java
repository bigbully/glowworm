
package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class BooleanArraySerializer implements ObjectSerializer {

    public static BooleanArraySerializer instance = new BooleanArraySerializer();

    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ARRAY_BOOLEAN);
        }
        boolean[] array = (boolean[]) object;
        serializer.writeBooleanArray(array);
    }
}
