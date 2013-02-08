package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class FloatArrayDeserializer implements ObjectDeserializer {

    public final static FloatArrayDeserializer instance = new FloatArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        float[] floats = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                floats = deserializer.scanFloatArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) floats;
    }
}
