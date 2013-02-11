package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;


public class CharArraySerializer implements ObjectSerializer {

    public static CharArraySerializer instance = new CharArraySerializer();

    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.ARRAY_CHAR);
        }
        char[] chars = (char[]) object;
        serializer.writeStringWithcharset(new String(chars));
    }

}
