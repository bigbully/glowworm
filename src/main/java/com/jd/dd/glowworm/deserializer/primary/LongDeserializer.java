package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class LongDeserializer implements ObjectDeserializer {

    public final static LongDeserializer instance = new LongDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Long value = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                value = deserializer.scanLong();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T) value;
    }
}
