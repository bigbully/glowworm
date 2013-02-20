package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class DoubleArraySerializer implements ObjectSerializer {

    public static final DoubleArraySerializer instance = new DoubleArraySerializer();

    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ARRAY_DOUBLE);
        }
        double[] array = (double[]) object;
        serializer.writeDoubleArray(array);
    }

}
