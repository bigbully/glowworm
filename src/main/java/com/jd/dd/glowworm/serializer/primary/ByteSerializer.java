
package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;


public class ByteSerializer implements ObjectSerializer {

    public static ByteSerializer instance = new ByteSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Byte value = (Byte) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.BYTE);
        }
        serializer.writeByte(value);
    }
}
