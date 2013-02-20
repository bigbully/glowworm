package com.jd.dd.glowworm.serializer.multi;


import com.jd.dd.glowworm.asm.Type;
import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListSerializer extends MultiSerializer implements ObjectSerializer {

    public final static ListSerializer instance = new ListSerializer();

    @Override
    public Object getEachElement(Object multi, int i) {
        return ((List) multi).get(i);
    }

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        int size = ((List) object).size();
        serializer.writeNaturalInt(size);
        if (!needWriteType) {
            if (isInterface(extraParams)) {
                writeActualType(serializer, object);
            }
            Class componentClazz = (Class) extraParams[0];
            if (componentClazz == Object.class) {//选择性写入类名(Object)
                writeObjectElement(serializer, object, size);
            } else {//都不写(Generic)
                writeElementWithGerenic(serializer, object, componentClazz, size);
            }
        } else {
            writeActualType(serializer, object);
            writeObjectElement(serializer, object, size);
        }
    }

    private void writeActualType(PBSerializer serializer, Object object) {
        Class clazz = object.getClass();
        if (ArrayList.class.isAssignableFrom(clazz)) {
            serializer.writeType(Type.LIST_ARRAYLIST);
        } else if (LinkedList.class.isAssignableFrom(clazz)) {
            serializer.writeType(Type.LIST_LINKEDLIST);
        } else if (clazz.getName().equals("java.util.Arrays$ArrayList")) {
            serializer.writeType(Type.LIST_ARRAYS_ARRAYLIST);
        } else {
            serializer.writeType(Type.Unknown);
        }
    }
}
