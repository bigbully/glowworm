package com.jd.dd.glowworm.deserializer.normal;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class ClassDeserializer implements ObjectDeserializer {

    public static final ClassDeserializer instance = new ClassDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Class value = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                String className = deserializer.scanString();
                value = Class.forName(className);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) value;
    }
}
