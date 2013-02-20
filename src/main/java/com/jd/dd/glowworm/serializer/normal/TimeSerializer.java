package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.sql.Time;

public class TimeSerializer implements ObjectSerializer {

    public final static TimeSerializer instance = new TimeSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Time value = (Time) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.TIME);
        }
        serializer.writeLong(value.getTime());
    }
}
