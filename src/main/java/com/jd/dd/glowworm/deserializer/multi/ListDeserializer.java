package com.jd.dd.glowworm.deserializer.multi;


import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListDeserializer extends MultiDeserialier implements ObjectDeserializer {

    public final static ListDeserializer instance = new ListDeserializer();

    @Override
    public void setEachElement(Object multi, int i, Object item) {
        ((List)multi).add(item);
    }

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        if (needConfirmExist) {//作为属性
            try {
                if (deserializer.isObjectExist()) {
                    return (T) getList(deserializer, type, extraParams);
                } else {
                    return (T) deserializer.getReference();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {//直接序列化
            return (T) getList(deserializer, type, extraParams);
        }
        return null;
    }

    private Object getList(PBDeserializer deserializer, Type type, Object[] extraParams) {
        Object list;
        if (isInterface(extraParams)) {
            list = getActualTypeObjectWhileInterface(deserializer);
        } else {
            list = createList(type);
        }
        int size = 0;
        try {
            size = deserializer.scanNaturalInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deserializer.addToObjectIndexMap(list, this);
        Class componentClazz;
        if (extraParams == null || extraParams.length == 0) {
            componentClazz = Object.class;
        } else {
            componentClazz = (Class) extraParams[0];
        }
        if (componentClazz == Object.class) {//选择性写入类名(Object)
            getObjectElement(deserializer, list, size);
        } else {//都不写(Generic)
            getElementWithGerenic(deserializer, list, componentClazz, size);
        }
        return list;
    }

    private Object createList(Type type) {
        Object list;
        Class<?> rawClass = getRawClass(type);
        if (rawClass == ArrayList.class) {
            list = new ArrayList();
        } else if (rawClass == LinkedList.class) {
            list = new LinkedList();
        } else {
            try {
                list = rawClass.newInstance();
            } catch (Exception e) {
                throw new PBException("create instane error, class " + rawClass.getName());
            }
        }
        return list;
    }

    private Object getActualTypeObjectWhileInterface(PBDeserializer deserializer) {
        Object list;
        int type = deserializer.scanType();
        if (type == com.jd.dd.glowworm.asm.Type.LIST_ARRAYLIST) {
            list = new ArrayList();
        } else if (type == com.jd.dd.glowworm.asm.Type.LIST_LINKEDLIST) {
            list = new LinkedList();
        } else {
            throw new PBException("不支持这种List类型!");
        }
        return list;
    }


}
