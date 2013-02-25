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

import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;

import java.io.IOException;

public abstract class MultiSerializer {

    public abstract Object getEachElement(Object multi, int i);

    protected boolean isInterface(Object[] extraParams) {
        return extraParams.length == 2 && (Boolean) extraParams[1];
    }

    //无泛型或泛型为Object
    protected void writeObjectElement(PBSerializer serializer, Object multi, int size) {
        try {
            ObjectSerializer preWriter = null;
            Class preClazz = null;
            for (int i = 0; i < size; i++) {
                Object item = getEachElement(multi, i);
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

    //有明确泛型
    protected void writeElementWithGerenic(PBSerializer serializer, Object multi, Class elementClazz, int size) {
        ObjectSerializer objectSerializer = serializer.getObjectWriter(elementClazz);
        boolean needConsiderRef = serializer.needConsiderRef(objectSerializer);
        for (int i = 0; i < size; i++) {
            Object item = getEachElement(multi, i);
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
}
