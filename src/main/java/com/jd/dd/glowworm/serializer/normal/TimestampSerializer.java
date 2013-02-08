package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;

public class TimestampSerializer implements ObjectSerializer {

    public final static TimestampSerializer instance = new TimestampSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Timestamp value = (Timestamp) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.TIMESTAMP);
        }
        serializer.writeLong(value.getTime());
    }
}
