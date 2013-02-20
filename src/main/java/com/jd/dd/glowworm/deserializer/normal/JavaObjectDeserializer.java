package com.jd.dd.glowworm.deserializer.normal;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;


public class JavaObjectDeserializer implements ObjectDeserializer {

    public static final JavaObjectDeserializer instance = new JavaObjectDeserializer();

    //这个反序列化器进行所有用Object声明的属性的反序列化
    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        int typeCode = deserializer.scanType();
        return (T) deserializer.parsePureObject(typeCode);
    }
}
