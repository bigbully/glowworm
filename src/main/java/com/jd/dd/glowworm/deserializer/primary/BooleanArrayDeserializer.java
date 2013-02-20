package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class BooleanArrayDeserializer implements ObjectDeserializer {

    public final static BooleanArrayDeserializer instance = new BooleanArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        boolean[] booleans = null;
        if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
            try {
                booleans = deserializer.scanBooleanArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) booleans;
    }

}
