package com.jd.dd.glowworm.deserializer.multi;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;
import java.util.*;

public class SetDeserializer extends MultiDeserialier implements ObjectDeserializer{

    public final static SetDeserializer instance = new SetDeserializer();

    @Override
    public void setEachElement(Object multi, int i, Object item) {
        ((Set)multi).add(item);
    }

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        if (needConfirmExist) {//作为属性
            try {
                if (deserializer.isObjectExist()) {
                    return (T) getSet(deserializer, type, extraParams);
                } else {
                    return (T) deserializer.getReference();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {//直接序列化
            return (T) getSet(deserializer, type, extraParams);
        }
        return null;
    }

    private Object getSet(PBDeserializer deserializer, Type type, Object[] extraParams) {
        Object set;
        if (isInterface(extraParams)) {
            set = getActualTypeObjectWhileInterface(deserializer);
        } else {
            set = createSet(type);
        }
        int size = 0;
        try {
            size = deserializer.scanNaturalInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deserializer.addToObjectIndexMap(set, this);
        Class componentClazz;
        if (extraParams == null || extraParams.length == 0) {
            componentClazz = Object.class;
        } else {
            componentClazz = (Class) extraParams[0];
        }
        if (componentClazz == Object.class) {//选择性写入类名(Object)
            getObjectElement(deserializer, set, size);
        } else {//都不写(Generic)
            getElementWithGerenic(deserializer, set, componentClazz, size);
        }
        return set;
    }

    private Object createSet(Type type) {
        Object set;
        Class<?> rawClass = getRawClass(type);
        if (rawClass == HashSet.class) {
            set = new HashSet();
        } else if (rawClass == TreeSet.class) {
            set = new TreeSet();
        } else {
            try {
                set = rawClass.newInstance();
            } catch (Exception e) {
                throw new PBException("create instane error, class " + rawClass.getName());
            }
        }
        return set;
    }

    private Object getActualTypeObjectWhileInterface(PBDeserializer deserializer) {
        Object set;
        int type = deserializer.scanType();
        if (type == com.jd.dd.glowworm.asm.Type.COLLECTION_HASHSET) {
            set = new HashSet();
        } else if (type == com.jd.dd.glowworm.asm.Type.COLLECTION_TREESET) {
            set = new HashSet();
        } else {
            throw new PBException("不支持这种Set类型!");
        }
        return set;
    }
}
