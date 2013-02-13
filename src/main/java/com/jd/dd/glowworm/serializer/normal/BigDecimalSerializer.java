
package com.jd.dd.glowworm.serializer.normal;

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalSerializer implements ObjectSerializer {

    public final static BigDecimalSerializer instance = new BigDecimalSerializer();

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        BigDecimal val = (BigDecimal) object;
        if (needWriteType) {
            serializer.writeType(com.jd.dd.glowworm.asm.Type.BIGDECIMAL);
        }
        serializer.writeString(val.toString());
    }
}
