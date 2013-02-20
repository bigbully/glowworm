package com.jd.dd.glowworm.deserializer.multi;


import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

public class ListDeserializer extends MultiDeserialier implements ObjectDeserializer {

    public final static ListDeserializer instance = new ListDeserializer();

    @Override
    public void setEachElement(Object multi, int i, Object item) {
        ((List) multi).add(item);
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
        if (isArrayListInArrays(type)) {//如果是直接序列化Arrays.ArrayList
            return ArrayListInArraysDeserializer.instance.handleAlone(deserializer);
        } else {
            Object list;
            if (isInterface(extraParams)) {
                list = getActualTypeObjectWhileInterface(deserializer);
            } else {
                list = createList(type);
            }
            if (isArrayListInArrays(list)) {
                return ArrayListInArraysDeserializer.instance.handleAsField(deserializer, type, extraParams);
            } else {
                return handleNormalList(deserializer, type, extraParams, list);
            }
        }
    }

    private boolean isArrayListInArrays(Type type) {
        if (type instanceof Class && ((Class) type).getName().equals("java.util.Arrays$ArrayList")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isArrayListInArrays(Object obj) {
        return obj == null ? true : false;
    }

    private Object handleNormalList(PBDeserializer deserializer, Type type, Object[] extraParams, Object list) {
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
        } else if (rawClass == Arrays.class) {
            list = null;
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
        } else if (type == com.jd.dd.glowworm.asm.Type.LIST_ARRAYS_ARRAYLIST) {
            list = null;
        } else {
            throw new PBException("不支持这种List类型!");
        }
        return list;
    }

    private static class ArrayListInArraysDeserializer extends MultiDeserialier {

        public static final ArrayListInArraysDeserializer instance = new ArrayListInArraysDeserializer();

        @Override
        public void setEachElement(Object multi, int i, Object item) {
            ((AbstractList) multi).set(i, item);
        }

        //按照没有泛型的list处理,直接序列化这个list
        private Object handleAlone(PBDeserializer deserializer) {
            AbstractList list;
            int size = 0;
            try {
                size = deserializer.scanNaturalInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
            list = (AbstractList) Arrays.asList(new Object[size]);
            deserializer.addToObjectIndexMap(list, null);
            getObjectElement(deserializer, list, size);
            return list;
        }

        //作为javabean的属性来处理
        public <T> Object handleAsField(PBDeserializer deserializer, Type type, Object[] extraParams) {
            AbstractList list;
            int size = 0;
            try {
                size = deserializer.scanNaturalInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Class componentClazz;
            if (extraParams == null || extraParams.length == 0) {
                componentClazz = Object.class;
            } else {
                componentClazz = (Class) extraParams[0];
            }
            Object array = Array.newInstance(componentClazz, size);
            list = (AbstractList) Arrays.asList((T[]) array);
            deserializer.addToObjectIndexMap(list, null);

            if (componentClazz == Object.class) {//选择性写入类名(Object)
                getObjectElement(deserializer, list, size);
            } else {//都不写(Generic)
                getElementWithGerenic(deserializer, list, componentClazz, size);
            }
            return list;
        }
    }

}
