package com.jd.dd.glowworm.deserializer.multi;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;
import com.jd.dd.glowworm.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MultiDeserialier {

    public abstract void setEachElement(Object multi, int i, Object item);

    //判断是否是解析属性
    protected boolean isScanField(Object[] extraParams) {
        if (extraParams != null && extraParams.length > 0) {
            return true;
        } else {
            return false;
        }
    }

    protected Class<?> getRawClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        } else {
            throw new PBException("TODO");
        }
    }

    protected boolean isInterface(Object[] extraParams) {
        return extraParams != null && extraParams.length == 2 && (Boolean) extraParams[1];
    }

    //有明确泛型
    protected void getElementWithGerenic(PBDeserializer deserializer, Object multi, Class elementClazz, int size) {
        ObjectDeserializer parser = deserializer.getDeserializer(elementClazz);
        for (int i = 0; i < size; i++) {
            if (deserializer.isObjectExist()) {
                Object item = parser.deserialize(deserializer, elementClazz, false);
                setEachElement(multi, i, item);
            } else {
                setEachElement(multi, i, deserializer.getReference());
            }
        }
    }

    //无泛型或泛型为Object
    protected void getObjectElement(PBDeserializer deserializer, Object multi, int size) {
        try {
            Class preClazz = null;
            ObjectDeserializer preParser = null;
            for (int i = 0; i < size; i++) {
                Object item;
                if (deserializer.isObjectExist()) {
                    int itemType = deserializer.scanType();
                    if (itemType == com.jd.dd.glowworm.asm.Type.OBJECT) {
                        String itemClassName = deserializer.scanString();
                        //如果与前一个反序列化器相同的话，类名为空字符串,直接使用上一个反序列化器，如果能读到类名，则直接用新的反序列化器
                        if (!itemClassName.equals("")) {
                            preClazz = TypeUtils.loadClass(itemClassName);
                            preParser = deserializer.getDeserializer(preClazz);
                        }
                        item = preParser.deserialize(deserializer, preClazz, false);
                    } else {
                        item = deserializer.parsePureObject(itemType);
                    }
                    setEachElement(multi, i, item);
                } else {
                    setEachElement(multi, i, deserializer.getReference());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Map getActualMapObjectWhileInterface(PBDeserializer deserializer) {
        Map actual = null;
        int typeCode = deserializer.scanType();
        if (typeCode == com.jd.dd.glowworm.asm.Type.MAP_HASH) {
            actual = new HashMap();
        } else if (typeCode == com.jd.dd.glowworm.asm.Type.MAP_LinkedHash) {
            actual = new LinkedHashMap();
        } else if (typeCode == com.jd.dd.glowworm.asm.Type.MAP_ConcurrentHashMap) {
            actual = new ConcurrentHashMap();
        }

        return actual;
    }

}
