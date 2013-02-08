package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class ShortArrayDeserializer implements ObjectDeserializer {

    public final static ShortArrayDeserializer instance = new ShortArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        short[] shorts = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                int size = deserializer.scanNaturalInt();
                shorts = new short[size];
                for (int i = 0; i < size; i++) {
                    shorts[i] = deserializer.scanShort();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) shorts;
    }
}
