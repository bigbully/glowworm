package com.jd.dd.glowworm.deserializer.normal;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class StringDeserializer implements ObjectDeserializer {

    public final static StringDeserializer instance = new StringDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        String value = null;
        try {
            value = deserializer.scanStringWithCharset();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T) value;
    }
}
