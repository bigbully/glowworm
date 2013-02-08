package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class ByteArrayDeserializer implements ObjectDeserializer {

    public final static ByteArrayDeserializer instance = new ByteArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        byte[] ret = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                ret = deserializer.scanByteArray();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return (T) ret;
    }
}
