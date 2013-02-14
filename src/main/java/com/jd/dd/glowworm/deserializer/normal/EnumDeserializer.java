package com.jd.dd.glowworm.deserializer.normal;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class EnumDeserializer implements ObjectDeserializer{

    private final Class<?> enumClass;

    public EnumDeserializer(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Enum value = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                value = deserializer.scanEnum(enumClass);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T)value;
    }
}
