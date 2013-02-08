package com.jd.dd.glowworm.serializer.multi;

import com.jd.dd.glowworm.asm.Type;
import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public class ArraySerializer extends MultiSerializer implements ObjectSerializer {

    public final static ArraySerializer instance = new ArraySerializer();

    @Override
    public Object getEachElement(Object array, int i) {
        return ((Object[]) array)[i];
    }

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        if (needWriteType) {
            serializer.writeType(Type.ARRAY);
        }
        Class elementClazz;
        if (!needWriteType) {//如果是在写javabean的属性，则会传extraParams
            elementClazz = (Class) extraParams[0];
        } else {//如果在序列化集合，则不传extraParams
            elementClazz = object.getClass().getComponentType();
        }
        Object[] array = (Object[]) object;
        int size = array.length;
        serializer.writeNaturalInt(size);
        if (elementClazz == Object.class) {//如果是Object[]
            writeObjectElement(serializer, array, size);
        } else {//如果是特定类型的数组
            writeElementWithGerenic(serializer, array, elementClazz, size);
        }
    }


}
