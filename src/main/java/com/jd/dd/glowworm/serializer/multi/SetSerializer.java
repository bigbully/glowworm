/*
 * Copyright 360buy
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.jd.dd.glowworm.serializer.multi;

import com.jd.dd.glowworm.asm.Type;
import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class SetSerializer extends MultiSerializer implements ObjectSerializer {

    public final static SetSerializer instance = new SetSerializer();

    @Override
    public Object getEachElement(Object multi, int i) {
        return ((Iterator) multi).next();
    }

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        int size = ((Set) object).size();
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

    //有明确泛型
    @Override
    protected void writeElementWithGerenic(PBSerializer serializer, Object multi, Class elementClazz, int size) {
        ObjectSerializer objectSerializer = serializer.getObjectWriter(elementClazz);
        boolean needConsiderRef = serializer.needConsiderRef(objectSerializer);
        Iterator iterator = ((Set) multi).iterator();
        for (int i = 0; i < size; i++) {
            Object item = getEachElement(iterator, i);
            if (item == null) {
                serializer.writeNull();
            } else {
                if (needConsiderRef && serializer.isReference(item)) {
                    serializer.writeNull();
                } else {
                    serializer.writeNotNull();
                    try {
                        objectSerializer.write(serializer, item, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void writeObjectElement(PBSerializer serializer, Object multi, int size) {
        try {
            ObjectSerializer preWriter = null;
            Class preClazz = null;
            Iterator iterator = ((Set) multi).iterator();
            for (int i = 0; i < size; i++) {
                Object item = getEachElement(iterator, i);
                if (item == null) {
                    serializer.writeNull();
                } else {
                    Class clazz = item.getClass();
                    if (clazz == preClazz) {
                        if (serializer.needConsiderRef(preWriter) && serializer.isReference(item)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString("");//写入空类名
                            }
                            preWriter.write(serializer, item, true);
                        }
                    } else {
                        preClazz = clazz;
                        preWriter = serializer.getObjectWriter(clazz);
                        if (serializer.needConsiderRef(preWriter) && serializer.isReference(item)) {
                            serializer.writeNull();
                        } else {
                            serializer.writeNotNull();
                            if (serializer.isAsmJavaBean(preWriter)) {
                                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                                serializer.writeString(clazz.getName());//写入类名
                            }
                            preWriter.write(serializer, item, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeActualType(PBSerializer serializer, Object object) {
        Class clazz = object.getClass();
        if (HashSet.class.isAssignableFrom(clazz)) {
            serializer.writeType(Type.COLLECTION_HASHSET);
        } else if (TreeSet.class.isAssignableFrom(clazz)) {
            serializer.writeType(Type.COLLECTION_TREESET);
        } else {
            serializer.writeType(Type.Unknown);
        }
    }
}
