package com.jd.dd.glowworm.deserializer.asm;

import com.jd.dd.glowworm.deserializer.reflect.FieldDeserializer;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.deserializer.reflect.JavaBeanDeserializer;
import com.jd.dd.glowworm.util.FieldInfo;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class ASMJavaBeanDeserializer implements ObjectDeserializer {

    protected InnerJavaBeanDeserializer serializer;

    public ASMJavaBeanDeserializer(PBDeserializer mapping, Class<?> clazz) {
        serializer = new InnerJavaBeanDeserializer(mapping, clazz);

        serializer.getFieldDeserializerMap();
    }

    public abstract Object createInstance(PBDeserializer parser, Type type);

    public InnerJavaBeanDeserializer getInnterSerializer() {
        return serializer;
    }

    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams){
        return (T) serializer.deserialize(deserializer, type, false);
    }

    public Object createInstance(PBDeserializer parser) {
        return serializer.createInstance(parser, serializer.getClazz());
    }

    public FieldDeserializer createFieldDeserializer(PBDeserializer mapping, Class<?> clazz, FieldInfo fieldInfo) {
        return mapping.createFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public FieldDeserializer getFieldDeserializer(String name) {
        return serializer.getFieldDeserializerMap().get(name);
    }

    public Type getFieldType(String name) {
        return serializer.getFieldDeserializerMap().get(name).getFieldType();
    }

    public boolean parseField(PBDeserializer parser, String key, Object object, Type objectType, Map<String, Object> fieldValues) {
        Map<String, FieldDeserializer> feildDeserializerMap = serializer.getFieldDeserializerMap();
        FieldDeserializer fieldDeserializer = feildDeserializerMap.get(key);

        if (fieldDeserializer == null) {
            for (Map.Entry<String, FieldDeserializer> entry : feildDeserializerMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    fieldDeserializer = entry.getValue();
                    break;
                }
            }
        }

        fieldDeserializer.parseField(parser, object, objectType, fieldValues);
        return true;
    }

    public final class InnerJavaBeanDeserializer extends JavaBeanDeserializer {

        private InnerJavaBeanDeserializer(PBDeserializer mapping, Class<?> clazz) {
            super(mapping, clazz);
        }

        public boolean parseField(PBDeserializer parser, String key, Object object, Type objectType, Map<String, Object> fieldValues) {
            return ASMJavaBeanDeserializer.this.parseField(parser, key, object, objectType, fieldValues);
        }

        public FieldDeserializer createFieldDeserializer(PBDeserializer mapping, Class<?> clazz, FieldInfo fieldInfo) {
            return ASMJavaBeanDeserializer.this.createFieldDeserializer(mapping, clazz, fieldInfo);
        }
    }

}
