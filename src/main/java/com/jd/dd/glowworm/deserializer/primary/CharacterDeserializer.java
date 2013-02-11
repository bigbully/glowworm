package com.jd.dd.glowworm.deserializer.primary;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.util.TypeUtils;

import java.lang.reflect.Type;

public class CharacterDeserializer implements ObjectDeserializer {
    public final static CharacterDeserializer instance = new CharacterDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Object value = null;
        try {
            if (needConfirmExist && deserializer.isObjectExist() || !needConfirmExist) {
                value = deserializer.scanStringWithCharset();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (T) TypeUtils.castToChar(value);
    }
}
