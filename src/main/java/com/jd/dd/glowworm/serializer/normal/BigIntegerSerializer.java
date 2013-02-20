package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerSerializer implements ObjectSerializer {

    public final static BigIntegerSerializer instance = new BigIntegerSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        BigInteger val = (BigInteger) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.BIGINTEGER);
        }
        serializer.writeString(val.toString());
    }
}
