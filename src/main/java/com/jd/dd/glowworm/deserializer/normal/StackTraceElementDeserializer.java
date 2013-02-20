package com.jd.dd.glowworm.deserializer.normal;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.deserializer.primary.IntegerDeserializer;

import java.lang.reflect.Type;

public class StackTraceElementDeserializer implements ObjectDeserializer {

    public static final StackTraceElementDeserializer instance = new StackTraceElementDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        Object obj;
        if (needConfirmExist) {//作为对象的属性
            if (deserializer.isObjectExist()) {
                obj = getStackTraceElement(deserializer);
            } else {
                obj = deserializer.getReference();
            }
        } else {
            obj = getStackTraceElement(deserializer);
        }
        return (T) obj;
    }

    private Object getStackTraceElement(PBDeserializer deserializer) {
        String className = StringDeserializer.instance.deserialize(deserializer, null, true);
        if (className != null && !className.equals("")) {
            String fileName = StringDeserializer.instance.deserialize(deserializer, String.class, true);
            Integer lineNumber = IntegerDeserializer.instance.deserialize(deserializer, Integer.class, true);
            String methodName = StringDeserializer.instance.deserialize(deserializer, String.class, true);
            return new StackTraceElement(className, methodName, fileName, lineNumber);
        } else {
            return null;
        }
    }
}
