package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;

public class CharArrayDeserializer implements ObjectDeserializer{

    public final static CharArrayDeserializer instance = new CharArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        String value = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                value = deserializer.scanString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return (T)value.toCharArray();
    }
}
