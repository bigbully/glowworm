package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class ByteDeserializer implements ObjectDeserializer {
    public final static ByteDeserializer instance = new ByteDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Byte value = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                value = deserializer.scanByte();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T) value;
    }
}
