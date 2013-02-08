package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class IntArrayDeserializer implements ObjectDeserializer {

    public final static IntArrayDeserializer instance = new IntArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        int[] ints = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                ints = deserializer.scanIntArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) ints;
    }
}
