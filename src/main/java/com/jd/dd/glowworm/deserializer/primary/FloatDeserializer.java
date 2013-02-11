package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class FloatDeserializer implements ObjectDeserializer {

    public final static FloatDeserializer instance = new FloatDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Float value = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                value = deserializer.scanFloat();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T) value;
    }
}
