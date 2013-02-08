
package com.jd.dd.glowworm.serializer.primary;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

public class CharacterSerializer implements ObjectSerializer {

    public final static CharacterSerializer instance = new CharacterSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Character value = (Character) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.CHAR);
        }
        serializer.writeString(value.toString());
    }
}
