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
package com.jd.dd.glowworm.serializer.reflect;

import com.jd.dd.glowworm.PBException;
import com.jd.dd.glowworm.serializer.ObjectSerializer;
import com.jd.dd.glowworm.serializer.PBSerializer;
import com.jd.dd.glowworm.util.FieldInfo;
import com.jd.dd.glowworm.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavaBeanSerializer implements ObjectSerializer {

    private final FieldSerializer[] sortedGetters;

    public JavaBeanSerializer(Class<?> clazz) {
        this(clazz, (Map<String, String>) null);
    }

    @Override
    public void write(PBSerializer serializer, Object object, boolean needWriteType, Object... extraParams) throws IOException {
        final FieldSerializer[] getters = this.sortedGetters;

        try {
            if (needWriteType) {
                serializer.writeType(com.jd.dd.glowworm.asm.Type.OBJECT);
                serializer.writeString(object.getClass().getName());
            }

            for (int i = 0; i < getters.length; ++i) {
                FieldSerializer fieldSerializer = getters[i];
                Field field = fieldSerializer.getField();
                if (field != null) {
                    if (Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }
                }

                Object propertyValue = fieldSerializer.getPropertyValue(object);
                fieldSerializer.writeProperty(serializer, propertyValue, i);
            }
        } catch (Exception e) {
            throw new PBException("write javaBean error", e);
        }
    }

    public JavaBeanSerializer(Class<?> clazz, Map<String, String> aliasMap) {
        {
            List<FieldSerializer> getterList = new ArrayList<FieldSerializer>();
            List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(clazz, aliasMap, true);

            for (FieldInfo fieldInfo : fieldInfoList) {
                getterList.add(createFieldSerializer(fieldInfo));
            }

            sortedGetters = getterList.toArray(new FieldSerializer[getterList.size()]);
        }
    }

    public FieldSerializer createFieldSerializer(FieldInfo fieldInfo) {
        return new ObjectFieldSerializer(fieldInfo);
    }
}
