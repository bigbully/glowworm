package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class DoubleArrayDeserializer implements ObjectDeserializer {

    public final static DoubleArrayDeserializer instance = new DoubleArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        double[] doubles = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                doubles = deserializer.scanDoubleArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) doubles;
    }
}
