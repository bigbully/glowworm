package com.jd.dd.glowworm.deserializer.multi;

import com.jd.dd.glowworm.deserializer.ObjectDeserializer;
import com.jd.dd.glowworm.deserializer.PBDeserializer;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

public class ArrayDeserializer extends MultiDeserialier implements ObjectDeserializer {

    public final static ArrayDeserializer instance = new ArrayDeserializer();

    @Override
    public <T> T deserialize(PBDeserializer deserializer, Type type, boolean needConfirmExist, Object... extraParams) {
        if (needConfirmExist) {
            try {
                if (deserializer.isObjectExist()) {
                    Class componentType;
                    if (!needConfirmExist) {
                        componentType = (Class) extraParams[0];
                    } else {
                        componentType = ((Class) type).getComponentType();
                    }
                    return (T) getArray(deserializer, componentType);
                } else {
                    return (T) deserializer.getReference();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Class componentType;
            if (needConfirmExist) {
                componentType = (Class) extraParams[0];
            } else {
                componentType = ((Class) type).getComponentType();
            }
            return (T) getArray(deserializer, componentType);
        }
        return null;
    }


    private Object getArray(PBDeserializer parser, Class componentType) {
        int size = 0;
        try {
            size = parser.scanNaturalInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object array = Array.newInstance(componentType, size);
        parser.addToObjectIndexMap(array, this);

        if (componentType == Object.class) {
            getObjectElement(parser, array, size);
        } else {
            getElementWithGerenic(parser, array, componentType, size);
        }
        return array;
    }

    @Override
    public void setEachElement(Object multi, int i, Object item) {
        ((Object[]) multi)[i] = item;
    }
}
