package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class ShortDeserializer implements ObjectDeserializer {
    public final static ShortDeserializer instance = new ShortDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Short value = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                value = deserializer.scanShort();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T) value;
    }
}
