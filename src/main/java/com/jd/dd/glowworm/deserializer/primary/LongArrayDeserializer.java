package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class LongArrayDeserializer implements ObjectDeserializer {

    public final static LongArrayDeserializer instance = new LongArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        long[] longs = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                longs = deserializer.scanLongArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) longs;
    }
}
