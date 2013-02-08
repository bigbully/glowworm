
package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

public class ShortSerializer implements ObjectSerializer {

    public static ShortSerializer instance = new ShortSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Number value = (Number) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.SHORT);
        }
        serializer.writeShort(value.shortValue());
    }
}
