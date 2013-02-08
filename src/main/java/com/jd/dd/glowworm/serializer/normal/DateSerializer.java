package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.util.Date;

public class DateSerializer implements ObjectSerializer {

    public final static DateSerializer instance = new DateSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        Date value = (Date) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.DATE);
        }
        serializer.writeLong(value.getTime());
    }
}
