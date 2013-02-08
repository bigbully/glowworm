
package com.jd.dd.glowworm.serializer.reflect;

import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.util.FieldInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class FieldSerializer implements Comparable<FieldSerializer> {

    protected final FieldInfo fieldInfo;
    private boolean writeNull = false;

    public FieldSerializer(FieldInfo fieldInfo) {
        super();
        this.fieldInfo = fieldInfo;
        fieldInfo.setAccessible(true);
    }

    public boolean isWriteNull() {
        return writeNull;
    }

    public Field getField() {
        return fieldInfo.getField();
    }

    public String getName() {
        return fieldInfo.getName();
    }

    public Method getMethod() {
        return fieldInfo.getMethod();
    }

    public int compareTo(FieldSerializer o) {
        return this.getName().compareTo(o.getName());
    }

    public Object getPropertyValue(Object object) throws Exception {
        return fieldInfo.get(object);
    }

    public abstract void writeProperty(PBSerializer serializer, Object propertyValue, int fieldInfoIndexParm) throws Exception;

}
